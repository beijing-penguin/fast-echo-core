ps -ef | grep fast-im-core-0.0.1-SNAPSHOT.jar | grep java | grep -v 'grep'  | awk '{print $2}' | xargs kill -9
