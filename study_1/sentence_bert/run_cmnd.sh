#!/bin/bash

echo "Performing Screen Localization (SL)"
python3 screen_and_component_localization.py SL

echo "Performing Component Localization (CL)"
python3 screen_and_component_localization.py CL
