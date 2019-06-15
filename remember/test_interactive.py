#! /usr/bin/env python
from __future__ import absolute_import
from builtins import object
import os
import subprocess
import sys
import unittest
from functools import partial

import remember.command_store_lib as command_store_lib
import remember.interactive as interactive
from remember.command_store_lib import Command
from remember.interactive import InteractiveCommandExecutor

TEST_PATH_DIR = os.path.dirname(os.path.realpath(__file__))
sys.path.insert(0, TEST_PATH_DIR + '/../')


class Test(unittest.TestCase):
    def test_command_update_info_should_correctly_set_info(self):
        interactive = InteractiveCommandExecutor()
        command = Command("git rest --hard HEAD")
        command_info = 'command info'
        self.set_input('1', command_info)
        interactive.set_command_info([command])
        self.assertEqual(command.get_command_info(), command_info)
        self.reset_input()

    def test_command_update_info_should_fail_set_info_becuase_exit(self):
        interactive = InteractiveCommandExecutor()
        command = Command("git rest --hard HEAD")
        self.set_input('exit')
        self.assertFalse(interactive.set_command_info([command]))
        self.assertEqual(command.get_command_info(), "")
        self.reset_input()

    def test_delete_command_from_store_should_delete(self):
        store = command_store_lib.CommandStore()
        command = Command("testing delete this command")
        store.add_command(command)
        self.assertEqual(store.get_num_commands(), 1)
        interactive = InteractiveCommandExecutor()
        self.set_input('1', 'y')
        self.assertTrue(interactive.delete_interaction(store, [command]))
        self.assertEqual(store.get_num_commands(), 0)
        self.reset_input()

    def test_delete_command_from_store_when_quit_should_not_delete(self):
        store = command_store_lib.CommandStore()
        command = Command("testing delete this command")
        store.add_command(command)
        self.assertEqual(store.get_num_commands(), 1)
        interactive = InteractiveCommandExecutor()
        self.set_input('quit')
        self.assertFalse(interactive.delete_interaction(store, [command]))
        self.assertEqual(store.get_num_commands(), 1)
        self.reset_input()

    def test_delete_command_from_store_when_no_should_not_delete(self):
        store = command_store_lib.CommandStore()
        command = Command("testing delete this command")
        store.add_command(command)
        self.assertEqual(store.get_num_commands(), 1)
        interactive = InteractiveCommandExecutor()
        self.set_input('1', 'n')
        self.assertFalse(interactive.delete_interaction(store, [command]))
        self.assertEqual(store.get_num_commands(), 1)
        self.reset_input()

    def test_delete_command_from_store_with_exit_input_should_not_delete(self):
        store = command_store_lib.CommandStore()
        command = Command("testing delete this command")
        store.add_command(command)
        self.assertEqual(store.get_num_commands(), 1)
        interactive = InteractiveCommandExecutor()
        self.set_input('quit')
        self.assertFalse(interactive.delete_interaction(store, [command]))
        self.assertEqual(store.get_num_commands(), 1)
        self.reset_input()

    def test_delete_command_from_store_with_all_should_remove_all(self):
        store = command_store_lib.CommandStore()
        command = Command("testing delete this command")
        command2 = Command("remove this also")
        self.assertEqual(store.get_num_commands(), 0)
        store.add_command(command)
        store.add_command(command2)
        self.assertEqual(store.get_num_commands(), 2)
        interactive = InteractiveCommandExecutor()
        self.set_input('allofthem', 'y')
        self.assertTrue(interactive.delete_interaction(store, [command, command2]))
        self.assertEqual(store.get_num_commands(), 0)
        self.reset_input()

    def test_delete_command_from_store_with_1_2_should_remove_all(self):
        store = command_store_lib.CommandStore()
        command = Command("testing delete this command")
        command2 = Command("remove this also")
        self.assertEqual(store.get_num_commands(), 0)
        store.add_command(command)
        store.add_command(command2)
        self.assertEqual(store.get_num_commands(), 2)
        interactive = InteractiveCommandExecutor()
        self.set_input('1, 2', 'y')
        self.assertTrue(interactive.delete_interaction(store, [command, command2]))
        self.assertEqual(store.get_num_commands(), 0)
        self.reset_input()

    def test_delete_command_from_store_with_invalid_should_remove_1(self):
        store = command_store_lib.CommandStore()
        command = Command("testing delete this command")
        command2 = Command("remove this also")
        self.assertEqual(store.get_num_commands(), 0)
        store.add_command(command)
        store.add_command(command2)
        self.assertEqual(store.get_num_commands(), 2)
        interactive = InteractiveCommandExecutor()
        self.set_input('1, 9', 'y')
        self.assertTrue(interactive.delete_interaction(store, [command, command2]))
        self.assertEqual(store.get_num_commands(), 1)
        self.reset_input()

    def test_delete_command_from_store_with_both_invalid_should_remove_0(self):
        store = command_store_lib.CommandStore()
        command = Command("testing delete this command")
        command2 = Command("remove this also")
        self.assertEqual(store.get_num_commands(), 0)
        store.add_command(command)
        store.add_command(command2)
        self.assertEqual(store.get_num_commands(), 2)
        interactive = InteractiveCommandExecutor()
        self.set_input('8, 9')
        self.assertFalse(interactive.delete_interaction(store, [command, command2]))
        self.assertEqual(store.get_num_commands(), 2)
        self.reset_input()

    def test_run_when_command_is_executed(self):
        old_call = subprocess.call
        store = command_store_lib.CommandStore()
        command_str = "testing delete this command"
        command = Command(command_str)
        command2 = Command("remove this also")
        test_call = partial(self.subprocess_call_mock, expected=command_str)
        subprocess.call = test_call
        self.assertEqual(store.get_num_commands(), 0)
        store.add_command(command)
        store.add_command(command2)
        self.assertEqual(store.get_num_commands(), 2)
        interactive = InteractiveCommandExecutor()
        self.set_input('1')
        self.assertTrue(interactive.run([command, command2]))
        self.reset_input()
        subprocess.call = old_call

    def set_input(self, *args):
        self.original_raw_input = interactive.get_user_input
        interactive.get_user_input = InputMock(args)

    def reset_input(self):
        if self.original_raw_input:
            interactive.get_user_input = self.original_raw_input

    def subprocess_call_mock(self, command_str, expected, shell):
        self.assertEqual(expected, command_str)
        self.assertTrue(shell)


class InputMock(object):
    def __init__(self, args):
        self.index = 0
        self.args = args

    def __call__(self, input):
        return_value = self.args[self.index % len(self.args)]
        self.index = self.index + 1
        return return_value


if __name__ == '__main__':
    unittest.main()
