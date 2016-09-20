#! /usr/bin/env python
"""
This Module contains the core logic for the remember functions.
"""
import cPickle as pickle
import os.path
import re


PROCCESSED_TO_TAG = '****** previous commands read *******'
PICKLE_FILE_NAME = 'pickle_file.pickle'
FILE_STORE_NAME = 'command_storage.txt'


class CommandStore(object):

    """
    This is the primary class that holds and interacts with all the commands.

    Provides command set manipulation methods and search capabilities.
    """

    def __init__(self):
        self._command_dict = {}

    def add_command(self, command):
        """This method adds a command to the store."""
        if command.get_unique_command_id() not in self._command_dict:
            self._command_dict[command.get_unique_command_id()] = command
        else:
            self._command_dict[command.get_unique_command_id()]\
                .increment_count()

    def delete_command(self, command_str):
        """This method deletes a command from the store. """
        return self._command_dict.pop(command_str, None)

    def has_command(self, command):
        """This method checks to see if a command is in the store. """
        return command.get_unique_command_id() in self._command_dict

    def has_command_by_name(self, command_str):
        """This method checks to see if a command (by name) is in the store.
        """
        return command_str in self._command_dict

    def get_num_commands(self):
        """This method returns the number of commands in the store."""
        return len(self._command_dict)

    def get_command_by_name(self, command_str):
        """This method gets the command by its full name."""
        if command_str in self._command_dict:
            return self._command_dict[command_str]
        return None

    def search_commands(self, command_strs, starts_with=False):
        """This method searches the command store for the command given."""
        matches = []
        for _, command in self._command_dict.iteritems():
            if starts_with:
                if (not command.get_unique_command_id()
                        .startswith(command_strs[0])):
                    continue
            if all(cmd_search in command.get_unique_command_id()
                    for cmd_search in command_strs):
                matches.append(command)
        return matches

    @staticmethod
    def pickle_command_store(command_store, file_name):
        """Pickle the whole store."""
        pickle.dump(command_store, open(file_name, "wb"))

    @staticmethod
    def load_command_store(file_name):
        """Load the command store from a pickle file."""
        return pickle.load(open(file_name, "rb"))

    @staticmethod
    def print_commands(commands):
        """Pretty print the commands."""
        for command in commands:
            print (command.get_unique_command_id() + " --count:"
                   + str(command.get_count_seen()))


class IgnoreRules(object):
    """ This class holds the set of ignore rules for commands."""
    def __init__(self):
        self._start_with = []
        self._contains = []
        self._matches = set()

    def is_match(self, command_str):
        """ If the command matches any of the ignore rules returns true."""
        # ignore all empty strings.
        if not command_str:
            return True
        if command_str in self._matches:
            return True
        for val in self._start_with:
            if command_str.startswith(val):
                return True
        for val in self._contains:
            if val in command_str:
                return True
        return False

    def add_starts_with(self, command_str):
        """Add a starts with rule to ignore."""
        self._start_with.append(command_str)

    def add_contains(self, command_str):
        """Add a contains with rule to ignore."""
        self._contains.append(command_str)

    def add_matches(self, command_str):
        """Add a exact matches with rule to ignore."""
        self._matches.add(command_str)

    @staticmethod
    def create_ignore_rule(src_file):
        """Generate a IgnoreRules object from the input file."""
        ignore_rules = IgnoreRules()
        methods = {
            's': ignore_rules.add_starts_with,
            'c': ignore_rules.add_contains,
            'm': ignore_rules.add_matches,
        }
        if not os.path.isfile(src_file):
            return ignore_rules
        for line in open(src_file).readlines():
            split = line.split(":", 1)
            if len(split) == 2:
                methods[split[0]](split[1].strip())
        return ignore_rules


class Command(object):
    """This class holds the basic pieces for a command."""

    def __init__(self, command_str=""):
        self._command_str = Command.get_currated_command(command_str)
        self._context_before = set()
        self._context_after = set()
        self._manual_comments = "Place any comments here."
        self._parse_command(self._command_str)
        self._count_seen = 1

    def _parse_command(self, command):
        """Set the primary command."""
        command_split = command.split(" ")
        if command_split[0] == ".":
            self._primary_command = command_split[1]
            self._command_args = command_split[2:]
        else:
            self._primary_command = command_split[0]
            self._command_args = command_split[1:]

    def get_command_args(self):
        """Get the input ars for the command"""
        return self._command_args

    def get_primary_command(self):
        """Get the primary command."""
        return self._primary_command

    def get_unique_command_id(self):
        """Get the commands unique id."""
        return self._command_str

    def increment_count(self):
        """Increment the count of the command."""
        self._count_seen += 1

    def get_count_seen(self):
        """Get the count seen."""
        return self._count_seen

    @staticmethod
    def get_currated_command(command_str):
        """Given a command string currate the string and return."""
        currated_command = re.sub(' +', ' ', command_str.strip())
        return currated_command


def get_unread_commands(src_file):
    """Read the history file and get all the unread commands."""
    unproccessed_lines = []
    for line in reversed(open(src_file).readlines()):
        if PROCCESSED_TO_TAG in line:
            return list(reversed(unproccessed_lines))
        unproccessed_lines.append(line.strip())
    return unproccessed_lines


def read_history_file(
        store,
        src_file,
        store_file,
        ignore_file=None,
        mark_read=True):
    """Read in the history files."""

    commands = get_unread_commands(src_file)
    output = []
    if ignore_file:
        ignore_rules = IgnoreRules.create_ignore_rule(ignore_file)
    else:
        ignore_rules = IgnoreRules()

    for line in commands:
        command = Command(line)
        if ignore_rules.is_match(command.get_unique_command_id()):
            continue
        store.add_command(command)
        output.append(command.get_unique_command_id())
    if mark_read:
        with open(store_file, 'a') as command_filestore:
            for command_str in output:
                command_filestore.write(command_str + '\n')
        with open(src_file, "a") as myfile:
            myfile.write(PROCCESSED_TO_TAG + "\n")


def get_pickle_file_path(directory_path):
    """Get the pickle file given the directory where the files is."""
    return os.path.join(directory_path, PICKLE_FILE_NAME)


def get_command_store(file_name):
    """Get the command store from the input file."""
    store = CommandStore()
    if os.path.isfile(file_name):
        print 'Unpacking pickle file ' + file_name
        store = CommandStore.load_command_store(file_name)
    else:
        print 'File not found: ' + file_name
    return store
