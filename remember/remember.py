#! /usr/bin/env python

import command_store_lib
import sys
import argparse



def main():
	args = sys.argv
	if len(args) != 3:
		print "To many or too few args.\n$> remember.py [file_store_directory_path] ['word|phrase to look up']"
		return
	store = command_store_lib.getCommandStore(command_store_lib.getPickleFilePath(args[1]))
	result = store.searchCommands(args[2])
	print "Number of results found: " + str(len(result))
	command_store_lib.CommandStore.printCommands(result)


if __name__ == "__main__":
	main()