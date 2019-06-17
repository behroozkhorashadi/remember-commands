import argparse
from unittest import TestCase

import mock

from remember import update_store
from remember.command_store_lib import CommandStore


class CommandStoreTest(CommandStore):
    def search_commands(self,
                        search_terms,
                        starts_with=False,
                        sort=True,
                        search_info=False):
        return ['result not used']


class TestMain(TestCase):
    @mock.patch('remember.command_store_lib.load_command_store', return_value=CommandStore())
    @mock.patch('remember.command_store_lib.save_command_store')
    def test_setup_args_for_update_but_nothing_happens_because_nothing_updated(self,
                                                                               save_store_mock,
                                                                               load_store_mock):
        with mock.patch('argparse.ArgumentParser.parse_args',
                        return_value=argparse.Namespace(json=True,
                                                        all=True,
                                                        startswith=True,
                                                        execute=False,
                                                        save_dir='save_dir',
                                                        history_file_path='hist',
                                                        delete=False,
                                                        updateinfo=False,
                                                        query='query')):
            update_store.main(None)
            save_store_mock.assert_not_called()

    @mock.patch('remember.command_store_lib.load_command_store', return_value=CommandStoreTest())
    @mock.patch('remember.command_store_lib.save_command_store')
    @mock.patch('remember.command_store_lib.print_commands')
    def test_setup_args_for_update_when_called_with_delete_store_updated(self,
                                                                         print_commands,
                                                                         save_store_mock,
                                                                         load_store_mock):
        with mock.patch('argparse.ArgumentParser.parse_args',
                        return_value=argparse.Namespace(json=True,
                                                        all=True,
                                                        startswith=True,
                                                        execute=False,
                                                        save_dir='save_dir',
                                                        history_file_path='hist',
                                                        delete=True,
                                                        updateinfo=False,
                                                        query='query')):
            command_executor_mock = mock.Mock()
            command_executor_mock.delete_interaction.return_value = True
            update_store.main(command_executor_mock)
            print_commands.assert_called_once()
            save_store_mock.assert_called_once()

    @mock.patch('remember.command_store_lib.load_command_store', return_value=CommandStoreTest())
    @mock.patch('remember.command_store_lib.save_command_store')
    def test_setup_args_for_update_when_called_with_update_store_updated(self,
                                                                         save_store_mock,
                                                                         load_store_mock):
        with mock.patch('argparse.ArgumentParser.parse_args',
                        return_value=argparse.Namespace(json=True,
                                                        all=True,
                                                        startswith=True,
                                                        execute=False,
                                                        save_dir='save_dir',
                                                        history_file_path='hist',
                                                        delete=False,
                                                        updateinfo=True,
                                                        query='query')):
            command_executor_mock = mock.Mock()
            command_executor_mock.set_command_info.return_value = True
            update_store.main(command_executor_mock)
            save_store_mock.assert_called_once()
