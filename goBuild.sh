#!/bin/bash -x

SBT_CONTAINER_VERSION="latest"
CURRENT_SERVICE_NAME="ubirch-avatar-service"
### DOCKER_REPO=tracklecontainerregistry-on.azurecr.io
DOCKER_REPO=ubirch

function init() {

  DEPENDENCY_LABEL=$GO_DEPENDENCY_LABEL_SBT_CONTAINER


  if [ -z ${DEPENDENCY_LABEL} ]; then
    SBT_CONTAINER_VERSION="latest"
  else
    SBT_CONTAINER_VERSION="v${DEPENDENCY_LABEL}"
  fi

  if [ -f Dockerfile.input  ]; then
    # clean up the artifact generated by the sbt build
    rm Dockerfile.input
  fi

}

function build_software() {

	# get local .ivy2
	rsync -r ~/.ivy2/ ./.ivy2/
	rsync -r ~/.coursier/ ./.coursier/
  	docker run --ulimit nofile=10000:10000 --user `id -u`:`id -g` --env AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID --env AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY  --volume=${PWD}:/build ubirch/sbt-build:${SBT_CONTAINER_VERSION} $1
	# write back to local .ivy2

  if [ $? -ne 0 ]; then
	  rsync -r ./.ivy2/ ~/.ivy2/
	  rsync -r ./.coursier/ ~/.coursier/
      echo "Docker build failed"
      exit 1
  else
	  rsync -r ./.ivy2/ ~/.ivy2/
	  rsync -r ./.coursier/ ~/.coursier/
  fi
}

function build_container() {
  # copy artefacts to TMP directory for faster build
  rm -rf TMP/
  mkdir -p TMP
  #get artifact names generated by Scala Build
  source Dockerfile.input
  if [ ! -f $SOURCE ]; then
    echo "Missing $SOURCE file \n did you run $0 assembly?"
    exit 1
  fi

  # get artefact name from Dockerfile

  tar cvf - $SOURCE | (cd TMP; tar xvf - )
  tar cvf - config/src/main/resources/ tools/ | (cd TMP; tar xvf - )
  cp Dockerfile.template TMP/Dockerfile
  #replace artefact name in start.sh
  sed -i.bak "s%@@build-artefact@@%$TARGET%g" TMP/tools/start.sh
  sed -i.bak "s%@@SOURCE@@%$SOURCE%g" TMP/Dockerfile
  sed -i.bak "s%@@TARGET@@%$TARGET%g" TMP/Dockerfile
  cd TMP


  if [ -z $GO_PIPELINE_LABEL ]; then
      # building without GoCD
      docker build --pull -t $DOCKER_REPO/$CURRENT_SERVICE_NAME:v$GO_PIPELINE_LABEL .
  else
      # build with GoCD
      docker build --pull -t $DOCKER_REPO/$CURRENT_SERVICE_NAME:v$GO_PIPELINE_LABEL \
      --build-arg GO_PIPELINE_NAME=$GO_PIPELINE_NAME \
      --build-arg GO_PIPELINE_LABEL=$GO_PIPELINE_LABEL \
      --build-arg GO_PIPELINE_COUNTER=$GO_PIPELINE_COUNTER \
      --build-arg GO_STAGE_COUNTER=$GO_STAGE_COUNTER \
      --build-arg GO_REVISION_GIT=$GO_REVISION_GIT .
  fi

  if [ $? -ne 0 ]; then
    echo "Docker build failed"
    exit 1
  fi

  # push Docker image
  docker push $DOCKER_REPO/$CURRENT_SERVICE_NAME
  docker push $DOCKER_REPO/$CURRENT_SERVICE_NAME:v$GO_PIPELINE_LABEL
  if [ $? -ne 0 ]; then
    echo "Docker push failed"
    exit 1
  fi
}

function container_tag () {
    label=$1
    docker pull $DOCKER_REPO/$CURRENT_SERVICE_NAME:v$GO_PIPELINE_LABEL
    docker tag $DOCKER_REPO/$CURRENT_SERVICE_NAME:v$GO_PIPELINE_LABEL $DOCKER_REPO/$CURRENT_SERVICE_NAME:$label
    docker push $DOCKER_REPO/$CURRENT_SERVICE_NAME:$label

}

case "$1" in
    build)
        init
        build_software "clean compile test"
        ;;
    assembly)
        build_software "clean server/assembly"
        ;;
    containerbuild)
        build_container
        ;;
    containertag)
        container_tag "latest"
        ;;
    containertagstable)
        container_tag "stable"
        ;;
    *)
        echo "Usage: $0 { build | assembly | containerbuild | containertag | containertagstable }"
        exit 1
esac

exit 0
