#!/bin/bash
coverage erase
coverage run --source=. command_store_lib_unittest.py
coverage run --source=. -a interactive_unittest.py
coverage html
open htmlcov/index.html
