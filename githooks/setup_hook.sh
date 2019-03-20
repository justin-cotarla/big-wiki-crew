#!/usr/bin/env bash

rm ../.git/hooks/commit-msg.sample
cp commit-msg ../.git/hooks/
chmod +x ../.git/hooks/commit-msg
