#!/bin/bash

[ ! -f "$1" ] && { echo "Error: file $1 not found."; exit 1; }

if [ -s "$1" ]
then
  if ! grep -q "$2" "$1"; then
   exit 1
  fi
else
  echo "Error: file $1 is empty";
  exit 1
fi