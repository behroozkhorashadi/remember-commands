========
Overview
========

.. start-badges

.. list-table::
    :stub-columns: 1

    * - docs
      - |docs|
    * - tests
      - | |travis| |requires|
        | |codecov|
    * - package
      - | |version| |wheel| |supported-versions| |supported-implementations|
        | |commits-since|

.. |docs| image:: https://readthedocs.org/projects/remember-commands/badge/?style=flat
    :target: https://readthedocs.org/projects/remember-commands
    :alt: Documentation Status

.. |travis| image:: https://travis-ci.org/behroozkhorashadi/remember-commands.svg?branch=master
    :alt: Travis-CI Build Status
    :target: https://travis-ci.org/behroozkhorashadi/remember-commands

.. |requires| image:: https://requires.io/github/behroozkhorashadi/remember-commands/requirements.svg?branch=master
    :alt: Requirements Status
    :target: https://requires.io/github/behroozkhorashadi/remember-commands/requirements/?branch=master

.. |codecov| image:: https://codecov.io/github/behroozkhorashadi/remember-commands/coverage.svg?branch=master
    :alt: Coverage Status
    :target: https://codecov.io/github/behroozkhorashadi/remember-commands

.. |version| image:: https://img.shields.io/pypi/v/remember.svg
    :alt: PyPI Package latest release
    :target: https://pypi.python.org/pypi/remember

.. |commits-since| image:: https://img.shields.io/github/commits-since/behroozkhorashadi/remember-commands/v0.1.0.svg
    :alt: Commits since latest release
    :target: https://github.com/behroozkhorashadi/remember-commands/compare/v0.1.0...master

.. |wheel| image:: https://img.shields.io/pypi/wheel/remember.svg
    :alt: PyPI Wheel
    :target: https://pypi.python.org/pypi/remember

.. |supported-versions| image:: https://img.shields.io/pypi/pyversions/remember.svg
    :alt: Supported versions
    :target: https://pypi.python.org/pypi/remember

.. |supported-implementations| image:: https://img.shields.io/pypi/implementation/remember.svg
    :alt: Supported implementations
    :target: https://pypi.python.org/pypi/remember


.. end-badges

Store and be able to search all you command line history.

* Free software: MIT license

Installation
============

::

    pip install remember

Documentation
=============

https://remember-commands.readthedocs.io/

Development
===========

To run the all tests run::

    tox

Note, to combine the coverage data from all the tox environments run:

.. list-table::
    :widths: 10 90
    :stub-columns: 1

    - - Windows
      - ::

            set PYTEST_ADDOPTS=--cov-append
            tox

    - - Other
      - ::

            PYTEST_ADDOPTS=--cov-append tox
