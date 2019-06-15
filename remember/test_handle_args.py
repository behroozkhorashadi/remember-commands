import argparse
from unittest import TestCase

import mock
from remember import handle_args

class TestHandleArgs(TestCase):
    @mock.patch('argparse.ArgumentParser.parse_args',return_value=argparse.Namespace(updateinfo='foo',
                                                                                     delete=True,
                                                                                     save_dir='save',
                                                                                     history_file_path='hist',
                                                                                     query='query'))
    def test_setup_args_for_update_when(self, mock_args):
        args = handle_args.setup_args_for_update()
        assert args.delete == True
        assert args.save_dir == 'save'
        assert args.history_file_path == 'hist'
        assert args.query == 'query'

    @mock.patch('argparse.ArgumentParser.parse_args', return_value=argparse.Namespace(json=True,
                                                                                      all=True,
                                                                                      startswith=True,
                                                                                      execute='foo',
                                                                                      save_dir='save',
                                                                                      history_file_path='hist',
                                                                                      query='query'))
    def test_setup_args_for_search(self, mock_args):
        args = handle_args.setup_args_for_search()
        assert args.json == True
        assert args.all == True
        assert args.startswith == True
        assert args.execute == 'foo'
        assert args.save_dir == 'save'
        assert args.history_file_path == 'hist'
        assert args.query == 'query'

    @mock.patch('argparse.ArgumentParser.parse_args', return_value=argparse.Namespace(json=True,
                                                                                      save_dir='save',
                                                                                      history_file_path='hist'))
    def test_setup_args_for_generate(self, mock_args):
        args = handle_args.setup_args_for_generate()
        assert args.json == True
        assert args.save_dir == 'save'
        assert args.history_file_path == 'hist'
