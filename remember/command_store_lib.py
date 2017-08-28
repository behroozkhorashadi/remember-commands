#! /usr/bin/env python
"""
This Module contains the core logic for the remember functions.
"""
import cPickle as pickle
import os.path
import re
import subprocess
import time
import sys


PROCCESSED_TO_TAG = '****** previous commands read *******'
PICKLE_FILE_NAME = 'pickle_file.pickle'
JSON_FILE_NAME = 'command_store.json'
FILE_STORE_NAME = 'command_storage.txt'
COMMAND_CMP = lambda x, y: cmp(y.last_used_time(), x.last_used_time())
class bcolors:
    HEADER = '\033[95m'
    OKBLUE = '\033[94m'
    OKGREEN = '\033[92m'
    YELLOW = '\033[33m'
    WARNING = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'


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
            store_command = self._command_dict[command.get_unique_command_id()]
            store_command._increment_count()
            store_command._update_time(command.last_used_time())

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

    def search_commands(self, command_strs, starts_with=False, sort=True):
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
        if sort:
            matches.sort(COMMAND_CMP)
        return matches


def print_commands(commands, highlighted_terms=[]):
    """Pretty print the commands."""
    x = 1
    for command in commands:
        command_str = command.get_unique_command_id()
        for term in highlighted_terms:
            command_str = command_str.replace(term, bcolors.OKGREEN + term + bcolors.YELLOW)
        print (bcolors.HEADER + '(' + str(x) + '): ' + bcolors.YELLOW + command_str
              + bcolors.OKBLUE+ " --count:" + str(command.get_count_seen()) + bcolors.ENDC)
        x = x + 1


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

    def __init__(self, command_str="", last_used=time.time()):
        self._command_str = Command.get_currated_command(command_str)
        self._context_before = set()
        self._context_after = set()
        self._manual_comments = "Place any comments here."
        self._parse_command(self._command_str)
        self._count_seen = 1
        self._last_used = last_used

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
        # This is for backwards compatability with earlier picked clases that
        # didn't have this field.
        if not hasattr(self, '_command_args'):
            self._parse_command(self.get_unique_command_id())
        return self._command_args

    def get_primary_command(self):
        """Get the primary command."""
        return self._primary_command

    def get_unique_command_id(self):
        """Get the commands unique id."""
        return self._command_str

    def get_count_seen(self):
        """Get the count seen."""
        return self._count_seen

    def _increment_count(self):
        """Increment the count of the command."""
        self._count_seen += 1

    def _update_time(self, updated_time):
        """Update the time to this new time."""
        self._last_used = updated_time

    def last_used_time(self):
        """Get the last used time in seconds from epoch"""
        if not hasattr(self, '_last_used'):
            self._last_used = 0
        return self._last_used

    @staticmethod
    def get_currated_command(command_str):
        """Given a command string currate the string and return."""
        currated_command = re.sub(' +', ' ', command_str.strip())
        if currated_command.startswith(":"):
            p = re.compile(";")
            m = p.search(currated_command)
            if m and len(currated_command) > m.start()+1:
                currated_command = currated_command[m.start() + 1:].strip()
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
    # get the max count
    current_time = time.time()
    for line in commands:
        current_time += 1
        command = Command(line, current_time)
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


def get_file_path(directory_path, use_json=False):
    """Get the pickle file given the directory where the files is."""
    if not use_json:
        return os.path.join(directory_path, PICKLE_FILE_NAME)
    return os.path.join(directory_path, JSON_FILE_NAME)


def _load_command_store_from_pickle(file_name):
    """Load the command store from a pickle file."""
    return pickle.load(open(file_name, "rb"))


def _load_command_store_from_json(file_name):
    """Load the command store from a pickle file."""
    encoding = sys.getdefaultencoding()
    try:
        import jsonpickle
    except ImportError:
        print (bcolors.FAIL + 'Trying to use jsonpickle but importing the module failed. Is it installed?'
               + bcolors.ENDC)
        return CommandStore()
    reload(sys)
    sys.setdefaultencoding('utf8')
    with open(file_name, "rb") as in_file:
        command_store = jsonpickle.decode(in_file.read())
        sys.setdefaultencoding(encoding)
        return command_store


def _pickle_command_store(command_store, file_name):
    """Pickle the whole store."""
    pickle.dump(command_store, open(file_name, "wb"))


def _jsonify_command_store(command_store, file_name):
    """Jsonify the whole store."""
    encoding = sys.getdefaultencoding()
    try:
        import jsonpickle
    except ImportError:
        print (bcolors.FAIL + 'Trying to use jsonpickle but importing the module failed. Is it installed?'
               + bcolors.ENDC)
    reload(sys)
    sys.setdefaultencoding('utf8')
    with open(file_name, "wb") as out_file:
        out_file.write(jsonpickle.encode(command_store))
    sys.setdefaultencoding(encoding)


def load_command_store(file_name, format_is_json=False):
    """Get the command store from the input file."""
    encoding = None
    if format_is_json:
        load_store_method = _load_command_store_from_json
    else:
        load_store_method = _load_command_store_from_pickle
    if os.path.isfile(file_name):
        print bcolors.OKBLUE + 'Unpacking json file ' + file_name + bcolors.ENDC
        store = load_store_method(file_name)
    else:
        store = CommandStore()
        print bcolors.FAIL + 'File not found: ' + file_name + bcolors.ENDC
    return store

def save_command_store(store, filename, from_json=False):
    if from_json:
        save_store_method = _jsonify_command_store
    else:
        save_store_method = _pickle_command_store
    save_store_method(store, filename)
