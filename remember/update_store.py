#! /usr/bin/env python

from __future__ import print_function
from __future__ import absolute_import

import os
from builtins import str

from future.moves import sys

top_level_dir = os.path.dirname(os.path.dirname(os.path.realpath(__file__)))
sys.path.append(top_level_dir)

import os
from builtins import str

from future.moves import sys

top_level_dir = os.path.dirname(os.path.dirname(os.path.realpath(__file__)))
sys.path.append(top_level_dir)
import remember.command_store_lib as command_store
from remember import handle_args
from remember.interactive import InteractiveCommandExecutor


def main(command_executor):
    """Entry point for this executable python module."""
    args = handle_args.setup_args_for_update()
    store_file_path = command_store.get_file_path(args.save_dir, args.json, args.sql)
    store_type = command_store.get_store_type(args.json, args.sql)
    store = command_store.load_command_store(store_file_path, store_type)
    print('Looking for all past commands with: ' + ", ".join(args.query))
    result = store.search_commands(args.query, args.startswith, )
    print("Number of results found: " + str(len(result)))
    store_updated = False
    if args.delete and len(result) > 0:
        print("Delete mode")
        command_store.print_commands(result, args.query)
        store_updated = store_updated or command_executor.delete_interaction(store, result)
    if args.updateinfo and len(result) > 0:
        print("Updating Info mode")
        store_updated = store_updated or command_executor.set_command_info(result)
    if store_updated:
        print("Updating Command Store...")
        command_store.save_command_store(store, store_file_path, args.json)


if __name__ == "__main__":
    main(InteractiveCommandExecutor())
