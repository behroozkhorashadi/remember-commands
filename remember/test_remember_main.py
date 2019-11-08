import argparse
from unittest import TestCase
from remember import remember_main
import mock

from remember.command_store_lib import CommandStore


class TestMain(TestCase):
    def test_setup_args_for_search_but_missing_save_dir_should_return_error_string(self):
        with mock.patch('argparse.ArgumentParser.parse_args',
                        return_value=argparse.Namespace(json=True,
                                                        sql=False,
                                                        all=True,
                                                        startswith=True,
                                                        execute=False,
                                                        save_dir=None,
                                                        history_file_path='hist',
                                                        query='query')):
            assert remember_main.main().startswith("To many or too few args")

    @mock.patch('argparse.ArgumentParser.parse_args', return_value=argparse.Namespace(json=True,
                                                                                      sql=False,
                                                                                      all=True,
                                                                                      startswith=True,
                                                                                      execute=False,
                                                                                      save_dir='save_dir',
                                                                                      history_file_path=None,
                                                                                      query='query'))
    def test_setup_args_for_search_but_missing_history_file_path_should_return_error_string(self, mock_args):
        assert remember_main.main().startswith("To many or too few args")

    @mock.patch('remember.command_store_lib.load_command_store', return_value=CommandStore())
    @mock.patch('remember.command_store_lib.print_commands')
    def test_setup_args_for_search_should_make_appropriate_calls_into_command_store_lib(self,
                                                                                        print_mock,
                                                                                        store_mock):
        with mock.patch('argparse.ArgumentParser.parse_args',
                        return_value=argparse.Namespace(json=True,
                                                        sql=False,
                                                        all=True,
                                                        startswith=True,
                                                        execute=False,
                                                        save_dir='save_dir',
                                                        history_file_path='hist',
                                                        query='query')):
            remember_main.main()
            store_mock.assert_called_once_with(mock.ANY, True)
            print_mock.assert_called_once()
