#!/usr/bin/python3

import requests
import sys
import argparse
import json
import re


def get_args():
    """ Python argument parser. """

    parser = argparse.ArgumentParser()
    parser.add_argument("--id", help="The job id of travis build.")
    parser.add_argument("--stage", help="Travis build stage name.")
    parser.add_argument("--token", help="Travis build auth token.")
    args = parser.parse_args()
    return args


def get_ci_logs(id, token):
    """ Download travis ci logs

    :params job_id: travis ci build number
    :params token: travis access token 
    :return: text body of the response
    """

    try:
        url = "https://api.travis-ci.com/v3/job/{}/log.txt".format(id)
        response = requests.get(url, headers={'Authorization': 'token ' + token})
        if response.status_code != 200 or "Sorry, we experienced an error." in response.text:
            raise Exception
        return response.text
    except Exception as error:
        slack_webhook(build_id=None, stage_name=None, url=url, message="Cannot download Travis CI logs")
        sys.exit("Cannot download Travis CI logs")


def parse_logs(response):
    """ Parses the travis log. 

    :params response: text from the ci log
    :return: dict that contains error information
    """

    start = "FAILURE: Build failed with an exception."
    end = "BUILD FAILED"

    # capture the substring between two strings
    capture_error = response[response.find(start)+len(start):response.find(end)]

    # get 15 lines above build fail
    additional_log = re.findall('((?:.*\n){15}).*FAILURE: Build failed with an exception.', response)

    return {
        "capture": capture_error,
        "additional": additional_log
    }


def slack_webhook(job_id, stage_name, error):
    """ Send summary to slack. """

    SLACK_URL = "https://hooks.slack.com/services/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"
    BASE_URL = "https://travis-ci.com/justin-cotarla/big-wiki-crew/jobs/{}".format(job_id)
    headers = {'content-type': 'application/json'}
    data = {
        "username": "TRAVIS CI PARSER SUMMARY",
        "text": "*Build id*: {} \n *Stage name:* {} \n *Job url*: {} \n\n *Summary* \n ``` {} \n\n {} ```".format(job_id, stage_name, BASE_URL, error["capture"], error["additional"])
    }
    requests.post(SLACK_URL, headers=headers, data=json.dumps(data))


if __name__ == "__main__":
    args = get_args()
    response = get_ci_logs(args.id, args.token)
    error = parse_logs(response)
    slack_webhook(args.id, args.stage, error)
