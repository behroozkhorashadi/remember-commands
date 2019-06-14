#! /usr/bin/env python

import command_store_lib
import handle_args
from interactive import InteractiveCommandExecutor


def main():
    """Entry point for this executable python module."""
    args = handle_args.setup_args_for_update()
    store_file_path = command_store_lib.get_file_path(args.save_dir, args.json)
    store = command_store_lib.load_command_store(store_file_path, args.json)
    print('Looking for all past commands with: ' + ", ".join(args.query))
    result = store.search_commands(args.query, args.startswith, )
    print("Number of results found: " + str(len(result)))
    store_updated = False
    command_executor = InteractiveCommandExecutor()
    if args.delete and len(result) > 0:
        print("Delete mode")
        command_store_lib.print_commands(result, args.query)
        store_updated = store_updated or command_executor.delete_interaction(store, result)
    if args.updateinfo and len(result) > 0:
        print("Updating Info mode")
        store_updated = store_updated or command_executor.set_command_info(result)
    if store_updated:
        print("Updating Command Store...")
        command_store_lib.save_command_store(store, store_file_path, args.json)


if __name__ == "__main__":
    main()
