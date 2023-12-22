import argparse
import multiprocessing as mp
import numpy as np
import os
import shutil
import torch
import yaml
import torch.multiprocessing
from torch import optim
torch.multiprocessing.set_sharing_strategy('file_system')
from PIL import Image
from transformers import (
    CLIPModel, CLIPProcessor,
    AdamW, get_scheduler
)
from torch.utils.data import DataLoader, RandomSampler


from data import create_dataset, create_sampler, create_loader, ImageCaptionCollator

def do_train(model, train_dl):
    train_loss = 0
    model.train()
    for bid, (batch, _) in enumerate(train_dl):
        if bid % 100 == 0:
            print("...{:d} training steps complete".format(bid))
        batch = {k: v.to(device) for k, v in batch.items()}
        outputs = model(**batch, return_loss=True)
        loss = outputs.loss
        train_loss += loss.detach().cpu().numpy()
        loss.backward()
        
        optimizer.step()
        # lr_scheduler.step()
        optimizer.zero_grad()

    print("...{:d} training steps COMPLETE".format(bid))
    return train_loss


def do_eval(model, eval_dl):
    model.eval()
    val_loss, val_acc, num_examples = 0, 0, 0
    for bid, (batch, _) in enumerate(eval_dl):
        if bid % 100 == 0:
            print("... {:d} validation steps complete".format(bid))
        batch = {k: v.to(device) for k, v in batch.items()}
        with torch.no_grad():
            outputs = model(**batch, return_loss=True)

        loss = outputs.loss
        val_loss += loss.detach().cpu().numpy()

        logits_per_image = outputs.logits_per_image
        probs = logits_per_image.softmax(dim=1)
        predictions = torch.argmax(probs, dim=-1)
        labels = torch.arange(len(predictions)).to(device)

        accuracy = torch.sum(predictions == labels)
        num_examples += len(predictions)
        val_acc += accuracy

    print("... {:d} validation steps COMPLETE".format(bid))
    val_acc = val_acc.detach().cpu().numpy() / num_examples
    return val_loss, val_acc


def save_checkpoint(model, model_dir, epoch):
    model.save_pretrained(os.path.join(model_dir, "ckpt-{:d}".format(epoch)))


def save_training_history(history, model_dir):
    fhist = open(os.path.join(model_dir, "history.tsv"), "a")
    for epoch, train_loss in history:
        fhist.write("{:d}\t{:.5f}\n".format(
            epoch, train_loss))
    fhist.close()


###################### main ###########################

parser = argparse.ArgumentParser()
parser.add_argument("config_file",  help="provides training params")
args = parser.parse_args()

config_file = args.config_file
print("config_file: ", config_file)
with open(config_file, "r") as fcfg:
    config = yaml.load(fcfg, Loader=yaml.SafeLoader)
print(config)

model_dir = os.path.join(config["models_dir"], 
                         os.path.basename(config_file).split(".")[0])
shutil.rmtree(model_dir, ignore_errors=True)
os.makedirs(model_dir)
train_dataset, val_datset = create_dataset("component_rico_dataset", config)
# train_dataset, val_datset = create_dataset("screen_all_ob_rico_dataset", config)
print("Dataset length:", len(train_dataset))

model = CLIPModel.from_pretrained("openai/clip-vit-base-patch32")
# checkpoint = torch.load(config['checkpoint_file'])
# model.load_state_dict(checkpoint)
processor = CLIPProcessor.from_pretrained("openai/clip-vit-base-patch32")

collator = ImageCaptionCollator(processor)
train_sampler = RandomSampler(train_dataset,
                              replacement=True,
                              num_samples=config["train_sample_size"])
train_dl = DataLoader(train_dataset, 
                      batch_size=config["train_batch_size"], 
                      # shuffle=True, 
                      sampler=train_sampler,
                      num_workers=mp.cpu_count() - 10, 
                      collate_fn=collator)
 validation_dl = DataLoader(validation_ds,
                            batch_size=config["validation_batch_size"],
                            num_workers=mp.cpu_count() - 1,
                            collate_fn=collator)


optimizer = optim.SGD(model.parameters(), lr=config["learning_rate"], weight_decay=config["weight_decay"])

num_training_steps = config["num_epochs"] * len(train_dl)


device = torch.device("cuda") if torch.cuda.is_available() else torch.device("cpu")
model.to(device)

history = []
for epoch in range(1, config["num_epochs"] + 1):
    train_loss = do_train(model, train_dl)
    val_loss, val_acc = do_eval(model, validation_dl)
    if epoch % config["checkpoint_interval"] == 0:
        save_checkpoint(model, model_dir, epoch)
        history.append((epoch, train_loss,))
        print("EPOCH {:d}, training loss: {:.3f}"
           .format(epoch,
            train_loss,
            #   val_loss,
            #   val_acc
            ))
save_training_history(history, model_dir)