#!/usr/bin/python3

import requests
import sys
import argparse


TOKEN = "FSKL2p-uO1lUaMI_UvPIyw"


def get_args():
    """ Python argument parser """

    parser = argparse.ArgumentParser()
    parser.add_argument("--id", help="The job id of travis build.")
    args = parser.parse_args()
    return args


def get_ci_logs(job_id):
    """ Download travis ci logs

    :params job_id: integer travis ci build number
    :return: text body of the response 
    """

    url = f"https://api.travis-ci.com/v3/job/{job_id}/log.txt?log.token={TOKEN}"

    try:
        response = requests.get(url)
        if response.status_code != 200 or "Sorry, we experienced an error." in response.text:
            raise Exception
        return response.text
    except Exception as error:
        sys.exit("Cannot download Travis CI logs")


def parse_logs(response):
    """ Parses the travis log """

    if "BUILD FAILED" in response:
        print(response.split("BUILD FAILED",1)[1])

    if "exited with 1" in response:
        print("ERROR")


if __name__ == "__main__":
    args = get_args()
    response = get_ci_logs(args.id)
    parse_logs(response)
