import math
def cosine_lr_schedule(optimizer, epoch, max_epoch, init_lr, min_lr):
    """Decay the learning rate"""
    lr = (init_lr - min_lr) * 0.5 * (1. + math.cos(math.pi * epoch / max_epoch)) + min_lr
    for param_group in optimizer.param_groups:
        param_group['lr'] = lr
        
def warmup_lr_schedule(optimizer, step, max_step, init_lr, max_lr):
    """Warmup the learning rate"""
    lr = min(max_lr, init_lr + (max_lr - init_lr) * step / max_step)
    for param_group in optimizer.param_groups:
        param_group['lr'] = lr    

def step_lr_schedule(optimizer, epoch, init_lr, min_lr, decay_rate):        
    """Decay the learning rate"""
    lr = max(min_lr, init_lr * (decay_rate**epoch))
    for param_group in optimizer.param_groups:
        param_group['lr'] = lr    
        
import numpy as np
import io
import os
import time
from collections import defaultdict, deque
import datetime

import torch
import torch.distributed as dist
import evaluation_metrics as em

class SmoothedValue(object):
    """Track a series of values and provide access to smoothed values over a
    window or the global series average.
    """

    def __init__(self, window_size=20, fmt=None):
        if fmt is None:
            fmt = "{median:.4f} ({global_avg:.4f})"
        self.deque = deque(maxlen=window_size)
        self.total = 0.0
        self.count = 0
        self.fmt = fmt

    def update(self, value, n=1):
        self.deque.append(value)
        self.count += n
        self.total += value * n

    def synchronize_between_processes(self):
        """
        Warning: does not synchronize the deque!
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
    def __init__(self, delimiter="\t"):
        self.meters = defaultdict(SmoothedValue)
        self.delimiter = delimiter

    def update(self, **kwargs):
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
        self.meters[name] = meter

    def log_every(self, iterable, print_freq, header=None):
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
    def __init__(self, *args, **kwargs):
        super(AttrDict, self).__init__(*args, **kwargs)
        self.__dict__ = self


def compute_acc(logits, label, reduction='mean'):
    ret = (torch.argmax(logits, dim=1) == label).float()
    if reduction == 'none':
        return ret.detach()
    elif reduction == 'mean':
        return ret.mean().item()

def compute_n_params(model, return_str=True):
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
    This function disables printing when not in master process
    """
    import builtins as __builtin__
    builtin_print = __builtin__.print

    def print(*args, **kwargs):
        force = kwargs.pop('force', False)
        if is_master or force:
            builtin_print(*args, **kwargs)

    __builtin__.print = print


def is_dist_avail_and_initialized():
    if not dist.is_available():
        return False
    if not dist.is_initialized():
        return False
    return True


def get_world_size():
    if not is_dist_avail_and_initialized():
        return 1
    return dist.get_world_size()


def get_rank():
    if not is_dist_avail_and_initialized():
        return 0
    return dist.get_rank()


def is_main_process():
    return get_rank() == 0


def save_on_master(*args, **kwargs):
    if is_main_process():
        torch.save(*args, **kwargs)


def init_distributed_mode(args):
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
    This is the RealOBQuery class which contains OB-ID, OB-text, and ground-truth.
    """
    def __init__(self, bug_id, ob_id, ob_in_title, bug_type, ob_category, ob_rating, ob_text, ground_truth):
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
    This is the OBQuery class which contains OB-ID, OB-text, and ground-truth.
    """

    def __init__(self, app_name, screen_id, ob_id, ob_text, ground_truth):
        self.app_name = app_name
        self.screen_id = screen_id
        self.ob_id = ob_id
        self.ob_text = ob_text
        self.ground_truth = ground_truth


def calculate_metrics(results_list):
    """
    This method will calculate the evaluation metrics.
    :param results_list: a list of lists which contains the results of all the applications or all the OBs
    :return: return the evaluation metrics
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
    This method will return the train, validation, and test set.
    :param json_file_path: path of the JSON file that contains the train, validation, and test set
    :return: return the train, validation, and test set
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
    This method will return the list of application names.
    :param query_folder_path: path of the folder that contains the query files of all the applications
    :return: return the list of application names
    """
    app_name_list = os.listdir(query_folder_path)
    if '.DS_Store' in app_name_list:
        app_name_list.remove('.DS_Store')
    app_name_list.sort()
    return app_name_list

def split_test_set_into_folds(test_set, num_folds):
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