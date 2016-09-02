#! /usr/bin/env python

import unittest

import command_store_lib
import os

class TestCommandStoreLib(unittest.TestCase):

    def test_CommandStore_isEmpty(self):
        command_store = command_store_lib.CommandStore()
        self.assertEqual(0, command_store.getNumCommands())

    def test_addCommandToStore(self):
        command_store = command_store_lib.CommandStore()
        self.assertEqual(0, command_store.getNumCommands())
        command_str = "some command string"
        command = command_store_lib.Command(command_str)
        command_store.addCommand(command)
        self.assertTrue(command_store.hasCommand(command))
        self.assertFalse(command_store.hasCommand(command_store_lib.Command("some other command")))
        self.assertEqual(1, command_store.getNumCommands())
        self.assertEqual(command, command_store.getCommandByName(command_str))
        self.assertEqual(None, command_store.getCommandByName("non existent command string"))

    def test_getPrimaryCommand_CheckcorrectlyIdPrimaryCommand(self):
        command_str = "some command string"
        command = command_store_lib.Command(command_str)
        self.assertEqual("some", command.getPrimaryCommand())
        command_str = "  some command string"
        command = command_store_lib.Command(command_str)
        self.assertEqual("some", command.getPrimaryCommand())
        command_str = " . some command string"
        command = command_store_lib.Command(command_str)
        self.assertEqual("some", command.getPrimaryCommand())

    def test_Command_checkConstructor(self):
        command = command_store_lib.Command(" git branch")
        self.assertEqual("git branch", command.getUniqueCommandId())
        command = command_store_lib.Command("git branch")
        self.assertEqual("git branch", command.getUniqueCommandId())

    def test_readFile(self):
        file_name = "test_files/test_input.txt"
        store = command_store_lib.CommandStore()
        command_store_lib.readHistoryFile(store, file_name, "doesntmatter", None,  False)
        self.assertTrue(store.hasCommandByName("vim somefile.txt"))
        self.assertTrue(store.hasCommandByName("rm somefile.txt"))
        self.assertTrue(store.hasCommandByName("whereis script"))
        self.assertTrue(store.hasCommandByName("vim /usr/bin/script"))
        self.assertFalse(store.hasCommandByName("vim somefil"))
        self.assertEqual(2, store.getCommandByName("rm somefile.txt").getCountSeen())

    def test_readFile_withIgnoreFile(self):
        file_name = "test_files/test_input.txt"
        store = command_store_lib.CommandStore()
        command_store_lib.readHistoryFile(store, file_name, "doesntmatter", "test_files/test_ignore_rule.txt",  False)
        self.assertFalse(store.hasCommandByName("vim somefile.txt"))
        self.assertTrue(store.hasCommandByName("rm somefile.txt"))
        self.assertTrue(store.hasCommandByName("whereis script"))
        self.assertFalse(store.hasCommandByName("vim /usr/bin/script"))
        self.assertFalse(store.hasCommandByName("vim somefil"))
        self.assertEqual(2, store.getCommandByName("rm somefile.txt").getCountSeen())
    
    def test_verifyPickle(self):
        file_name = "test_pickle.txt"
        command_store = command_store_lib.CommandStore()
        command_str = "git branch"
        command = command_store_lib.Command(command_str)
        command_store.addCommand(command)
        command_store_lib.CommandStore.pickleCommandStore(command_store, file_name)
        command_store = command_store_lib.CommandStore.loadCommandStore(file_name)
        self.assertTrue(command_store.hasCommand(command))
        os.remove(file_name)

    def test_readUnproccessedLinesOnly(self):
        file_name = "test_files/test_processed.txt"
        unread_commands = command_store_lib.getUnReadCommands(file_name)
        self.assertEqual("vim somefile.txt", unread_commands[0])
        self.assertEqual("git commit -a -m \"renamed directory.\"", unread_commands[1])
        self.assertEqual(2, len(unread_commands))

    def test_CurratedCommands_ReturnCorrectResults(self):
        self.assertEqual("git foo", command_store_lib.Command.getCurratedCommand("    git     foo"))
        self.assertEqual(". git foo", command_store_lib.Command.getCurratedCommand(" .   git     foo"))

    def test_delete_whenExists_shouldDeleteFromStore(self):
        file_name = "test_files/test_input.txt"
        store = command_store_lib.CommandStore()
        command_store_lib.readHistoryFile(store, file_name, "doesntmatter", None, False)
        self.assertTrue(store.hasCommandByName("vim somefile.txt"))
        self.assertIsNotNone(store.deleteCommand('vim somefile.txt'))
        self.assertFalse(store.hasCommandByName("vim somefile.txt"))

    def test_delete_whenDoesntExists_shouldDeleteFromStore(self):
        store = command_store_lib.CommandStore()
        self.assertIsNone(store.deleteCommand('anything'))

    def test_ignoreRule_whenCreate_shouldCreateWorkingIgnoreRule(self):
        file_name = "test_files/test_ignore_rule.txt"
        ignore_rule = command_store_lib.IgnoreRules.createIgnoreRule(file_name)
        self.assertTrue(ignore_rule.isMatch('vim opensomefile'))
        self.assertFalse(ignore_rule.isMatch('svim opensomefile'))
        self.assertTrue(ignore_rule.isMatch('vim foo'))
        self.assertFalse(ignore_rule.isMatch('svim foos'))
        self.assertTrue(ignore_rule.isMatch('git commit -a -m'))
        self.assertFalse(ignore_rule.isMatch('git comit -a -m'))
        self.assertFalse(ignore_rule.isMatch('git foos'))


if __name__ == '__main__':
    unittest.main()