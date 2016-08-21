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
        command_store_lib.readHistoryFile(store, file_name, "doesntmatter", False)
        self.assertTrue(store.hasCommandByName("vim somefile.txt"))
        self.assertTrue(store.hasCommandByName("rm somefile.txt"))
        self.assertTrue(store.hasCommandByName("whereis script"))
        self.assertTrue(store.hasCommandByName("vim /usr/bin/script"))
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



if __name__ == '__main__':
    unittest.main()