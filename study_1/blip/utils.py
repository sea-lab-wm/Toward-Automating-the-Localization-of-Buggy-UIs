import numpy as np
import os
import time
from collections import defaultdict, deque
import datetime

import torch
import torch.distributed as dist
import evaluation_metrics as em

class SmoothedValue(object):
    """
    Tracks a series of values and provides smoothed statistics over a sliding window

    Maintains both recent values (in a rolling window) and global statistics across
    all values ever added. Useful for monitoring training metrics like loss or accuracy.

    Attributes:
        deque -- Rolling window of recent values (max size = window_size)
        total -- Sum of all values ever added
        count -- Number of values ever added
        fmt -- String format for display
    """

    def __init__(self, window_size=20, fmt=None):
        """
        Initializes a SmoothedValue tracker

        Arguments:
            window_size -- Maximum number of recent values to keep (default: 20)
            fmt -- Format string for __str__ method (default: "{median:.4f} ({global_avg:.4f})")
        Returns:
            None
        """
        if fmt is None:
            fmt = "{median:.4f} ({global_avg:.4f})"
        self.deque = deque(maxlen=window_size)
        self.total = 0.0
        self.count = 0
        self.fmt = fmt

    def update(self, value, n=1):
        """
        Adds a new value to the tracker and updates statistics

        Arguments:
            value -- The value to add
            n -- Weight/count for this value (default: 1)
        Returns:
            None (modifies object state in-place)
        """
        self.deque.append(value)
        self.count += n
        self.total += value * n

    def synchronize_between_processes(self):
        """
        Synchronizes count and total statistics across distributed training processes

        Uses PyTorch distributed operations to aggregate statistics from all GPU processes.
        Warning: Does NOT synchronize the deque (only total and count)!

        Arguments:
            None
        Returns:
            None (modifies object state in-place)
        """
        if not is_dist_avail_and_initialized():
            return
        t = torch.tensor([self.count, self.total], dtype=torch.float64, device='cuda')
        dist.barrier()
        dist.all_reduce(t)
        t = t.tolist()
        self.count = int(t[0])
        self.total = t[1]

    @property
    def median(self):
        d = torch.tensor(list(self.deque))
        return d.median().item()

    @property
    def avg(self):
        d = torch.tensor(list(self.deque), dtype=torch.float32)
        return d.mean().item()

    @property
    def global_avg(self):
        return self.total / self.count

    @property
    def max(self):
        return max(self.deque)

    @property
    def value(self):
        return self.deque[-1]

    def __str__(self):
        return self.fmt.format(
            median=self.median,
            avg=self.avg,
            global_avg=self.global_avg,
            max=self.max,
            value=self.value)


class MetricLogger(object):
    """
    Manages multiple SmoothedValue objects for tracking different metrics simultaneously

    Provides convenient methods for updating multiple metrics at once and logging
    progress during training loops. Each metric is stored as a SmoothedValue object.

    Attributes:
        meters -- Dictionary mapping metric names to SmoothedValue objects
        delimiter -- String separator for displaying metrics (default: tab)
    """

    def __init__(self, delimiter="\t"):
        """
        Initializes a MetricLogger

        Arguments:
            delimiter -- String to separate metrics when printing (default: tab)
        Returns:
            None
        """
        self.meters = defaultdict(SmoothedValue)
        self.delimiter = delimiter

    def update(self, **kwargs):
        """
        Updates multiple metrics at once using keyword arguments

        Arguments:
            **kwargs -- Metric names and values (e.g., loss=0.5, accuracy=0.85)
        Returns:
            None (modifies meters in-place)
        """
        for k, v in kwargs.items():
            if isinstance(v, torch.Tensor):
                v = v.item()
            assert isinstance(v, (float, int))
            self.meters[k].update(v)

    def __getattr__(self, attr):
        if attr in self.meters:
            return self.meters[attr]
        if attr in self.__dict__:
            return self.__dict__[attr]
        raise AttributeError("'{}' object has no attribute '{}'".format(
            type(self).__name__, attr))

    def __str__(self):
        loss_str = []
        for name, meter in self.meters.items():
            loss_str.append(
                "{}: {}".format(name, str(meter))
            )
        return self.delimiter.join(loss_str)

    def global_avg(self):
        loss_str = []
        for name, meter in self.meters.items():
            loss_str.append(
                "{}: {:.4f}".format(name, meter.global_avg)
            )
        return self.delimiter.join(loss_str)    
    
    def synchronize_between_processes(self):
        for meter in self.meters.values():
            meter.synchronize_between_processes()

    def add_meter(self, name, meter):
        """
        Adds a custom SmoothedValue meter to track a specific metric

        Arguments:
            name -- Name of the metric
            meter -- SmoothedValue object to track this metric
        Returns:
            None (modifies meters dictionary in-place)
        """
        self.meters[name] = meter

    def log_every(self, iterable, print_freq, header=None):
        """
        Wraps an iterable (like a training dataloader) with periodic logging

        Yields items from the iterable while printing progress, metrics, ETA, and
        memory usage at regular intervals. This is the main method for training loop logging.

        Arguments:
            iterable -- Data source to iterate over (e.g., DataLoader)
            print_freq -- Print statistics every N iterations
            header -- Optional header string to display (e.g., "Training Epoch 5")
        Returns:
            Generator that yields items from iterable
        """
        i = 0
        if not header:
            header = ''
        start_time = time.time()
        end = time.time()
        iter_time = SmoothedValue(fmt='{avg:.4f}')
        data_time = SmoothedValue(fmt='{avg:.4f}')
        space_fmt = ':' + str(len(str(len(iterable)))) + 'd'
        log_msg = [
            header,
            '[{0' + space_fmt + '}/{1}]',
            'eta: {eta}',
            '{meters}',
            'time: {time}',
            'data: {data}'
        ]
        if torch.cuda.is_available():
            log_msg.append('max mem: {memory:.0f}')
        log_msg = self.delimiter.join(log_msg)
        MB = 1024.0 * 1024.0
        for obj in iterable:
            data_time.update(time.time() - end)
            yield obj
            iter_time.update(time.time() - end)
            if i % print_freq == 0 or i == len(iterable) - 1:
                eta_seconds = iter_time.global_avg * (len(iterable) - i)
                eta_string = str(datetime.timedelta(seconds=int(eta_seconds)))
                if torch.cuda.is_available():
                    print(log_msg.format(
                        i, len(iterable), eta=eta_string,
                        meters=str(self),
                        time=str(iter_time), data=str(data_time),
                        memory=torch.cuda.max_memory_allocated() / MB))
                else:
                    print(log_msg.format(
                        i, len(iterable), eta=eta_string,
                        meters=str(self),
                        time=str(iter_time), data=str(data_time)))
            i += 1
            end = time.time()
        total_time = time.time() - start_time
        total_time_str = str(datetime.timedelta(seconds=int(total_time)))
        print('{} Total time: {} ({:.4f} s / it)'.format(
            header, total_time_str, total_time / len(iterable)))
        

class AttrDict(dict):
    """
    Dictionary subclass that allows attribute-style access to keys

    Enables accessing dictionary values using dot notation (obj.key) in addition
    to standard bracket notation (obj['key']), making config objects more readable.

    Example:
        config = AttrDict({'lr': 0.001, 'batch_size': 32})
        print(config.lr)  # Same as config['lr']
    """

    def __init__(self, *args, **kwargs):
        """
        Initializes an AttrDict with the same arguments as a regular dict

        Arguments:
            *args -- Positional arguments passed to dict constructor
            **kwargs -- Keyword arguments passed to dict constructor
        Returns:
            None
        """
        super(AttrDict, self).__init__(*args, **kwargs)
        self.__dict__ = self


def compute_acc(logits, label, reduction='mean'):
    """
    Calculates classification accuracy from model logits and true labels

    Compares predicted class (argmax of logits) with true labels and computes
    the proportion or per-sample correctness.

    Arguments:
        logits -- Raw model outputs (batch_size x num_classes tensor)
        label -- True class labels (batch_size tensor)
        reduction -- 'mean' for average accuracy, 'none' for per-sample (default: 'mean')
    Returns:
        Float (mean accuracy) or Tensor (per-sample accuracy) depending on reduction
    """
    ret = (torch.argmax(logits, dim=1) == label).float()
    if reduction == 'none':
        return ret.detach()
    elif reduction == 'mean':
        return ret.mean().item()

def compute_n_params(model, return_str=True):
    """
    Counts the total number of trainable parameters in a PyTorch model

    Iterates through all model parameters and sums up their sizes, useful for
    understanding model complexity and memory requirements.

    Arguments:
        model -- PyTorch model (nn.Module)
        return_str -- If True, returns formatted string (e.g., "12.5M"), else raw count (default: True)
    Returns:
        String like "12.5M" or "345.6K" if return_str=True, else integer count
    """
    tot = 0
    for p in model.parameters():
        w = 1
        for x in p.shape:
            w *= x
        tot += w
    if return_str:
        if tot >= 1e6:
            return '{:.1f}M'.format(tot / 1e6)
        else:
            return '{:.1f}K'.format(tot / 1e3)
    else:
        return tot

def setup_for_distributed(is_master):
    """
    Configures print function to only output on the master process in distributed training

    Overrides the built-in print function to suppress output on worker processes,
    preventing duplicate log messages when training on multiple GPUs.

    Arguments:
        is_master -- Boolean indicating if current process is the master (rank 0)
    Returns:
        None (modifies built-in print function globally)
    """
    import builtins as __builtin__
    builtin_print = __builtin__.print

    def print(*args, **kwargs):
        force = kwargs.pop('force', False)
        if is_master or force:
            builtin_print(*args, **kwargs)

    __builtin__.print = print


def is_dist_avail_and_initialized():
    """
    Checks if PyTorch distributed training is available and has been initialized

    Arguments:
        None
    Returns:
        Boolean -- True if distributed training is ready, False otherwise
    """
    if not dist.is_available():
        return False
    if not dist.is_initialized():
        return False
    return True


def get_world_size():
    """
    Returns the total number of processes in distributed training

    Arguments:
        None
    Returns:
        Integer -- Number of processes (GPUs), or 1 if not using distributed training
    """
    if not is_dist_avail_and_initialized():
        return 1
    return dist.get_world_size()


def get_rank():
    """
    Returns the rank (process ID) of the current process in distributed training

    Arguments:
        None
    Returns:
        Integer -- Process rank (0 to world_size-1), or 0 if not using distributed training
    """
    if not is_dist_avail_and_initialized():
        return 0
    return dist.get_rank()


def is_main_process():
    """
    Checks if the current process is the main/master process (rank 0)

    Arguments:
        None
    Returns:
        Boolean -- True if this is the master process, False otherwise
    """
    return get_rank() == 0


def save_on_master(*args, **kwargs):
    """
    Saves a PyTorch model checkpoint only on the master process

    Prevents redundant checkpoint saves when training on multiple GPUs. Only
    the rank 0 process will perform the save operation.

    Arguments:
        *args -- Positional arguments passed to torch.save()
        **kwargs -- Keyword arguments passed to torch.save()
    Returns:
        None
    """
    if is_main_process():
        torch.save(*args, **kwargs)


def init_distributed_mode(args):
    """
    Initializes PyTorch distributed training from environment variables

    Detects the distributed training environment (standard or SLURM cluster),
    sets up the process group, and configures printing for the master process.

    Arguments:
        args -- Argument object (will be modified with rank, world_size, gpu, distributed attributes)
    Returns:
        None (modifies args object in-place)
    """
    if 'RANK' in os.environ and 'WORLD_SIZE' in os.environ:
        args.rank = int(os.environ["RANK"])
        args.world_size = int(os.environ['WORLD_SIZE'])
        args.gpu = int(os.environ['LOCAL_RANK'])
    elif 'SLURM_PROCID' in os.environ:
        args.rank = int(os.environ['SLURM_PROCID'])
        args.gpu = args.rank % torch.cuda.device_count()
    else:
        print('Not using distributed mode')
        args.distributed = False
        return

    args.distributed = True

    torch.cuda.set_device(args.gpu)
    args.dist_backend = 'nccl'
    print('| distributed init (rank {}, word {}): {}'.format(
        args.rank, args.world_size, args.dist_url), flush=True)
    torch.distributed.init_process_group(backend=args.dist_backend, init_method=args.dist_url,
                                         world_size=args.world_size, rank=args.rank)
    torch.distributed.barrier()
    setup_for_distributed(args.rank == 0)


class RealOBQuery:
    """
    Stores information about a real bug from the dataset for UI localization experiments

    Contains all metadata about an Observed Behavior (OB) from actual bug reports,
    including the bug description text used as a query and ground truth screen/component IDs.

    Attributes:
        bug_id -- Unique identifier for the bug report
        ob_id -- Observed Behavior ID within the bug report
        ob_in_title -- Boolean indicating if OB is mentioned in the bug title
        bug_type -- Category of the bug (e.g., visual, functional, crash)
        ob_category -- Classification of the observation
        ob_rating -- Severity or importance rating of the OB
        ob_text -- Textual description of the bug (used as search query)
        ground_truth -- List of correct screen/component IDs that exhibit this bug
    """
    def __init__(self, bug_id, ob_id, ob_in_title, bug_type, ob_category, ob_rating, ob_text, ground_truth):
        """
        Initializes a RealOBQuery with bug metadata and ground truth

        Arguments:
            bug_id -- Unique bug identifier
            ob_id -- Observed Behavior ID
            ob_in_title -- Boolean, is OB in bug report title?
            bug_type -- Type/category of bug
            ob_category -- OB categorization
            ob_rating -- Severity rating
            ob_text -- Bug description text (the query)
            ground_truth -- List of correct screen/component IDs
        Returns:
            None
        """
        self.bug_id = bug_id
        self.ob_id = ob_id
        self.ob_in_title = ob_in_title
        self.bug_type = bug_type
        self.ob_category = ob_category
        self.ob_rating = ob_rating
        self.ob_text = ob_text
        self.ground_truth = ground_truth

class OBQuery:
    """
    Simplified query structure for synthetic bugs or basic UI localization experiments

    Contains minimal information needed for a bug query without the extensive metadata
    of RealOBQuery. Used for synthetic data or simpler experimental setups.

    Attributes:
        app_name -- Name of the application being tested
        screen_id -- Identifier for the screen containing the bug
        ob_id -- Observed Behavior ID
        ob_text -- Textual description of the bug (used as search query)
        ground_truth -- List of correct screen/component IDs
    """

    def __init__(self, app_name, screen_id, ob_id, ob_text, ground_truth):
        """
        Initializes an OBQuery with basic bug information

        Arguments:
            app_name -- Application name
            screen_id -- Screen identifier
            ob_id -- Observed Behavior ID
            ob_text -- Bug description text (the query)
            ground_truth -- List of correct screen/component IDs
        Returns:
            None
        """
        self.app_name = app_name
        self.screen_id = screen_id
        self.ob_id = ob_id
        self.ob_text = ob_text
        self.ground_truth = ground_truth


def calculate_metrics(results_list):
    """
    Computes standard Information Retrieval evaluation metrics for UI bug localization

    Takes binary relevance vectors (from ranking results) and calculates Mean Reciprocal Rank,
    Mean Average Precision, and Hit@K metrics for K=1 to 10. These metrics measure how well
    the model ranks buggy screens/components.

    Arguments:
        results_list -- List of binary result vectors, where each vector has 1s at positions
                       of relevant (buggy) items and 0s elsewhere. Example: [[0,1,0,0,...], [1,0,0,...]]
    Returns:
        Tuple of 12 floats: (mrr, map, hit_1, hit_2, ..., hit_10)
            mrr -- Mean Reciprocal Rank (average of 1/rank_of_first_correct)
            map -- Mean Average Precision
            hit_K -- Proportion of queries with correct answer in top K results
    """
    mrr = em.mean_reciprocal_rank(results_list)
    # print(f'MRR:{mrr}')
    map = em.mean_average_precision(results_list)
    # print(f'MAP:{map}')
    hit_1 = em.mean_hit_rate_at_k(results_list, 1)
    hit_2 = em.mean_hit_rate_at_k(results_list, 2)
    hit_3 = em.mean_hit_rate_at_k(results_list, 3)
    hit_4 = em.mean_hit_rate_at_k(results_list, 4)
    hit_5 = em.mean_hit_rate_at_k(results_list, 5)
    hit_6 = em.mean_hit_rate_at_k(results_list, 6)
    hit_7 = em.mean_hit_rate_at_k(results_list, 7)
    hit_8 = em.mean_hit_rate_at_k(results_list, 8)
    hit_9 = em.mean_hit_rate_at_k(results_list, 9)
    hit_10 = em.mean_hit_rate_at_k(results_list, 10)
    return mrr, map, hit_1, hit_2, hit_3, hit_4, hit_5, hit_6, hit_7, hit_8, hit_9, hit_10

import json
def get_train_validation_and_test_set(json_file_path):
    """
    Loads pre-defined train, validation, and test dataset splits from a JSON file

    Reads a JSON file containing three lines, each with a set of application/bug IDs
    for training, validation, and testing. Ensures consistent data splits across experiments.

    Expected JSON format (one object per line):
        {"train_set": ["app1", "app2", ...]}
        {"validate_set": ["app3", "app4", ...]}
        {"test_set": ["app5", "app6", ...]}

    Arguments:
        json_file_path -- Path to JSON file containing dataset splits
    Returns:
        Tuple of three lists: (train_set_list, validation_set_list, test_set_list)
    """
    # Load the JSON file for getting the train, validate, and test set
    with open(json_file_path, 'r') as json_file:
        data = [json.loads(line) for line in json_file]
        train_set = data[0]
        validation_set = data[1]
        test_set = data[2]
        train_set_list = list(train_set['train_set'])
        validation_set_list = list(validation_set['validate_set'])
        test_set_list = list(test_set['test_set'])
    return train_set_list, validation_set_list, test_set_list


def get_app_names_list(query_folder_path):
    """
    Retrieves a sorted list of application names from a directory

    Lists all items in the specified folder (typically application directories),
    removes system files like .DS_Store, and returns a sorted list of app names.

    Arguments:
        query_folder_path -- Path to folder containing application subdirectories
    Returns:
        List of application names (sorted alphabetically)
    """
    app_name_list = os.listdir(query_folder_path)
    if '.DS_Store' in app_name_list:
        app_name_list.remove('.DS_Store')
    app_name_list.sort()
    return app_name_list

def split_test_set_into_folds(test_set, num_folds):
    """
    Divides test data into N folds for cross-validation experiments

    Randomly shuffles the test set and splits it into approximately equal-sized folds.
    The last fold may be slightly larger if the data doesn't divide evenly.

    Arguments:
        test_set -- List of test samples (e.g., bug IDs or app names)
        num_folds -- Number of folds to create
    Returns:
        List of folds, where each fold is a list of test samples
    """
    num_samples = len(test_set)
    indices = np.arange(num_samples)
    np.random.shuffle(indices)
    fold_size = num_samples // num_folds
    folds = []
    for i in range(num_folds):
        fold_start = i * fold_size
        fold_end = (i + 1) * fold_size if i < num_folds - 1 else num_samples
        fold_indices = indices[fold_start:fold_end]
        fold_data = [test_set[index] for index in fold_indices]
        folds.append(fold_data)
    return folds