#!/bin/bash

_file="$1"
[ ! -f "$_file" ] && { echo "Error: file $1 not found."; exit 1; }

if [ -s "$_file" ]
then
  exit 0
else
  echo "Error: file $1 is empty";
  exit 1
fi