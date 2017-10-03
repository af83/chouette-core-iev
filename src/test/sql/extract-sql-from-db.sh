#!/bin/bash

WORK_DIR=`dirname $(readlink -f $0)`
VERBOSE=""

database=chouette2
user=chouette
host=localhost
port=5432
datatype="--schema-only"



function usage(){
    echo "Usage `basename $0`  [-p port] [-d database] [-u user] [-t tables separated with space] [-o outputfile] [-h host] -s [schema | data]"
    echo "  example: `basename $0`  -p 5433 -d chouette -u chouette -t 'compliances* referentiel' -o toto.sql -h localhost -s "
}

while getopts p:d:u:t:o:h:s: option
do
 case $option in
  p)
   port=$OPTARG
   ;;
  d)
   database=$OPTARG
   ;;
  u)
   user=$OPTARG
   ;; 
  t)
   tables=$OPTARG
   ;;
  o)
   output=$OPTARG
   ;;
  h)
   host=$OPTARG
   ;; 
  s)
   if [ "$OPTARG" == "data" ]; then
	datatype="--data-only --column-inserts"
   fi
   ;; 
 esac
done

if [ "${output}" == "" ] || [ "${tables}" == "" ]; then
	usage
	
else
	opt_tables="";
	for i in ${tables}; do
		opt_tables="${opt_tables} --table=${i}"
	done
	CMD="pg_dump ${datatype} --format=plain --file=${output} ${opt_tables} --username=${user} --host=${host} --port=${port} ${database}"
	echo "cmd=$CMD"
	$CMD
fi

	










