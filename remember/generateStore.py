#! /usr/bin/env python

import sys
import command_store_lib as com_lib
import os.path

FILE_STORE_NAME = 'command_storage.txt'


def main():
	args = sys.argv
	if len(args) != 3:
		print "To many or too few args.\n$> generateStore.py [history_file_path] [file_store_directory_path]"
		return
	pick_file_path = com_lib.getPickleFilePath(args[2])
	commands_file_path = os.path.join(args[2], FILE_STORE_NAME)
	store = com_lib.getCommandStore(pick_file_path)
	com_lib.readHistoryFile(store, args[1], commands_file_path)
	print 'Reading ' + args[1]
	com_lib.CommandStore.pickleCommandStore(store, pick_file_path)
	print 'Writing file out to ' + pick_file_path


if __name__ == "__main__":
	main()