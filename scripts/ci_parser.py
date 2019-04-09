#!/usr/bin/python3

import requests
import sys
import argparse
import json


SLACK_URL = "https://hooks.slack.com/services/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"


def get_args():
    """ Python argument parser """

    parser = argparse.ArgumentParser()
    parser.add_argument("--id", help="The job id of travis build.")
    parser.add_argument("--stage", help="Travis build stage name.")
    parser.add_argument("--url", help="Travis job url link.")
    parser.add_argument("--token", help="Travis build auth token.")
    args = parser.parse_args()
    return args


def get_ci_logs(id, token):
    """ Download travis ci logs

    :params job_id: integer travis ci build number
    :return: text body of the response
    """

    try:
        url = "https://api.travis-ci.com/v3/job/{}/log.txt".format(id)
        response = requests.get(url, headers={'Authorization': 'token ' + token})
        print(response.text)
        if response.status_code != 200 or "Sorry, we experienced an error." in response.text:
            raise Exception
        return response.text
    except Exception as error:
        slack_webhook(build_id=None, stage_name=None, message="Cannot download Travis CI logs")
        sys.exit("Cannot download Travis CI logs")


def parse_logs(response):
    """ Parses the travis log """

    # TODO parse the text for summaries
    if "BUILD FAILED" in response:
        return "build failed"

    if "exited with 1" in response:
        return "error"

    return "No errors :)"


def slack_webhook(build_id, stage_name, url, message):
    headers = {'content-type': 'application/json'}
    data = {
        "username": "TRAVIS CI PARSER SUMMARY",
        "text": "BUILD ID: {} \n STAGE NAME: {} \n INFO: {} \n JOB URL: {}".format(build_id, stage_name, message, url)
    }
    requests.post(SLACK_URL, headers=headers, data=json.dumps(data))


if __name__ == "__main__":
    args = get_args()
    print(args.id)
    print(args.stage)
    print(args.url)
    print(args.token)
    response = get_ci_logs(args.id, args.token)
    message = parse_logs(response)
    slack_webhook(args.id, args.stage, args.url, message)
