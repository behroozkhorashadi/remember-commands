import os
import sys

from cli import main

TEST_PATH_DIR = os.path.dirname(os.path.realpath(__file__))
sys.path.insert(0, TEST_PATH_DIR + '/../')


def test_main():
    main([])
