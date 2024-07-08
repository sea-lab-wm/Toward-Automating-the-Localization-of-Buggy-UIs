from transformers import BertTokenizer
import sys
sys.path.append("/home/yang/Projects/BLIP")
from torchvision import transforms
from torchvision.transforms.functional import InterpolationMode

import torch
from torch import nn
import torch.nn.functional as F
from PIL import Image, ImageDraw
from models.blip import create_vit, init_tokenizer, load_checkpoint
from transform.randaugment import RandomAugment
normalize = transforms.Normalize((0.48145466, 0.4578275, 0.40821073), (0.26862954, 0.26130258, 0.27577711))
transform_train = transforms.Compose([                        
        transforms.RandomResizedCrop(384,scale=(0.5, 1.0),interpolation=InterpolationMode.BICUBIC),
        transforms.RandomHorizontalFlip(),
        RandomAugment(2,5,isPIL=True,augs=['Identity','AutoContrast','Brightness','Sharpness','Equalize',
                                            'ShearX', 'ShearY', 'TranslateX', 'TranslateY', 'Rotate']),     
        transforms.ToTensor(),
        normalize,
    ])        

image_size = 384
vit = 'base'
vit_grad_ckpt = False
vit_ckpt_layer = 0                   
embed_dim = 256  
queue_size = 57600
momentum = 0.995
visual_encoder, vision_width = create_vit(vit, image_size, vit_grad_ckpt, vit_ckpt_layer,)
image_path_1 = "/home/yang/Projects/all_dataset/synthetic_data/component_images/adidas.app3/32637/0.png"
image_path_2 = "/home/yang/Projects/all_dataset/synthetic_data/component_images/adidas.app3/32637/1.png"
image_1 = Image.open(image_path_1)
image_2 = Image.open(image_path_2)

image_1 = image_1.convert("RGB")
image_1 = transform_train(image_1)

image_2 = image_2.convert("RGB")
image_2 = transform_train(image_2)
print(image_1.shape)

# combine image_1 and image_2 to a batch
images = [image_1, image_2]

# Create a batch by stacking images along a new dimension
batch = torch.stack(images)
print(batch.shape)




image_embeds = visual_encoder(batch) # (batch_size, num_patches + 1, embed_dim)
print(image_embeds.shape)

