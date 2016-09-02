#! /usr/bin/env python

import sys
import re
import cPickle as pickle
import os.path
import string


PROCCESSED_TO_TAG = '****** previous commands read *******'
PICKLE_FILE_NAME = 'pickle_file.pickle'
FILE_STORE_NAME = 'command_storage.txt'


class CommandStore:
	def __init__(self):
		self._command_dict = {}

	def addCommand(self, command):
		if command.getUniqueCommandId() not in self._command_dict:
			self._command_dict[command.getUniqueCommandId()] = command
		else:
			self._command_dict[command.getUniqueCommandId()].incrementCount()

	def deleteCommand(self, command_str):
		return self._command_dict.pop(command_str, None)

	def hasCommand(self, command):
		return command.getUniqueCommandId() in self._command_dict

	def hasCommandByName(self, command_str):
		return command_str in self._command_dict

	def getNumCommands(self):
		return len(self._command_dict)

	def getCommandByName(self, command_str):
		if command_str in self._command_dict:
			return self._command_dict[command_str]
		return None

	def searchCommands(self, command_str):
		matches = []
		print 'Looking for all past commands with: ' + command_str
		for key, command in self._command_dict.iteritems():
			if command_str in command.getUniqueCommandId():
				matches.append(command)
		return matches


	@staticmethod
	def pickleCommandStore(command_store, file_name):
		pickle.dump( command_store, open( file_name, "wb" ) )

	@staticmethod
	def loadCommandStore(file_name):
		return pickle.load( open( file_name, "rb" ) )

	@staticmethod
	def printCommands(commands):
		for command in commands:
			print command.getUniqueCommandId() +  " --count:" + str(command.getCountSeen())

class IgnoreRules:
	def __init__(self):
		self._start_with = []
		self._contains = []
		self._matches = set()

	def isMatch(self, command_str):
		if command_str in self._matches:
			return True
		for val in self._start_with:
			if command_str.startswith(val):
				return True
		for val in self._contains:
			if val in command_str:
				return True
		return False

	def addStartsWith(self, command_str):
		self._start_with.append(command_str)

	def addContains(self, command_str):
		self._contains.append(command_str)

	def addMatches(self, command_str):
		self._matches.add(command_str)

	@staticmethod
	def createIgnoreRule(srcFile):
		ignoreRules = IgnoreRules()
		methods = {
		's' : ignoreRules.addStartsWith,
		'c' : ignoreRules.addContains,
		'm' : ignoreRules.addMatches,
		}
		for line in reversed(open(srcFile).readlines()):
			split = line.split(":", 1)
			if len(split) == 2:
				methods[split[0]](split[1].strip())
		return ignoreRules


class Command:
	def __init__(self, command_str):
		self._command_str = Command.getCurratedCommand(command_str)
		self._context_before = set()
		self._context_after = set()
		self._manual_comments = "Place any comments here."
		self.setPrimaryCommand(self._command_str)
		self._count_seen = 1

	def setPrimaryCommand(self, command):
		command_split = command.split(" ")
		if command_split[0] == ".":
			self._primary_command = command_split[1]	
		else:
			self._primary_command = command_split[0]

	def getPrimaryCommand(self):
		return self._primary_command

	def getUniqueCommandId(self):
		return self._command_str

	def incrementCount(self):
		self._count_seen += 1

	def getCountSeen(self):
		return self._count_seen

	@staticmethod
	def getCurratedCommand(command_str):
		# removed excess spacing and weird characters
		currated_command = filter(lambda x: x in string.printable, command_str)
		currated_command =  re.sub(' +',' ', currated_command.strip())
		return currated_command


def getUnReadCommands(srcFile):
	unproccessed_lines = []
	for line in reversed(open(srcFile).readlines()):
		if PROCCESSED_TO_TAG in line:
			return list(reversed(unproccessed_lines))
		unproccessed_lines.append(line.strip())
	return unproccessed_lines


def readHistoryFile(store, srcFile, store_file, ignore_file=None, markRead=True):
	
	commands = getUnReadCommands(srcFile)
	output = []
	if ignore_file:
		ignore_rules = IgnoreRules.createIgnoreRule(ignore_file)
	else:
		ignore_rules = IgnoreRules()

	for line in commands:
		command = Command(line)
		if ignore_rules.isMatch(command.getUniqueCommandId()):
			continue
		store.addCommand(command)
		output.append(command.getUniqueCommandId())
	if markRead:
		with open(store_file, 'a') as command_filestore:
			for x in output:
				command_filestore.write(x + '\n') 
		with open(srcFile, "a") as myfile:
			myfile.write(PROCCESSED_TO_TAG + "\n")
		
def getPickleFilePath(directory_path):
	return os.path.join(directory_path, PICKLE_FILE_NAME)


def getCommandStore(file_name):
	store = CommandStore()
	if os.path.isfile(file_name):
		print 'Unpacking pickle file ' + file_name
		store = CommandStore.loadCommandStore(file_name)
	else:
		print 'File not found: ' + file_name
	return store
