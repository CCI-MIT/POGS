# Etherpad Lite Dockerfile
#
# https://github.com/ether/etherpad-docker
#
# Author: muxator
#
# Version 0.1

FROM node:lts-slim
LABEL maintainer="Etherpad team, https://github.com/ether/etherpad-lite"

# git hash of the version to be built.
# If not given, build the latest development version.
ARG ETHERPAD_VERSION=develop

# plugins to install while building the container. By default no plugins are
# installed.
# If given a value, it has to be a space-separated, quoted list of plugin names.
#
# EXAMPLE:
#   ETHERPAD_PLUGINS="ep_codepad ep_author_neat"
ARG ETHERPAD_PLUGINS="ep_disable_chat"

# Set the following to production to avoid installing devDeps
# this can be done with build args (and is mandatory to build ARM version)
ARG NODE_ENV=production

ARG EP_HOME=
ARG EP_UID=5001
ARG EP_GID=0
ARG EP_SHELL=
RUN groupadd --system ${EP_GID:+--gid "${EP_GID}" --non-unique} etherpad && \
    useradd --system ${EP_UID:+--uid "${EP_UID}" --non-unique} --gid etherpad \
        ${EP_HOME:+--home-dir "${EP_HOME}"} --create-home \
        ${EP_SHELL:+--shell "${EP_SHELL}"} etherpad

ARG EP_DIR=/opt/etherpad-lite


RUN apt-get update; apt-get install curl -y

# grab the ETHERPAD_VERSION tarball from github (no need to clone the whole
# repository)
RUN echo "Getting version: ${ETHERPAD_VERSION}" && \
	curl \
		--location \
		--fail \
		--silent \
		--show-error \
		--output /opt/etherpad-lite.tar.gz \
		https://github.com/ether/etherpad-lite/archive/"${ETHERPAD_VERSION}".tar.gz && \
	mkdir /opt/etherpad-lite && \
	tar xf /opt/etherpad-lite.tar.gz \
		--directory /opt/etherpad-lite \
		--strip-components=1 && \
	rm /opt/etherpad-lite.tar.gz

RUN chown etherpad:etherpad "${EP_DIR}"
WORKDIR /opt/etherpad-lite

# Plugins must be installed before installing Etherpad's dependencies, otherwise
# npm will try to hoist common dependencies by removing them from
# src/node_modules and installing them in the top-level node_modules. As of
# v6.14.10, npm's hoist logic appears to be buggy, because it sometimes removes
# dependencies from src/node_modules but fails to add them to the top-level
# node_modules. Even if npm correctly hoists the dependencies, the hoisting
# seems to confuse tools such as `npm outdated`, `npm update`, and some ESLint
# rules.
RUN { [ -z "${ETHERPAD_PLUGINS}" ] || \
      npm install --no-save --legacy-peer-deps ${ETHERPAD_PLUGINS}; } && \
    src/bin/installDeps.sh && \
    rm -rf ~/.npm


#COPY ep_pogs /opt/etherpad-lite/node_modules/ep_pogs enable to use the local copy of plugin
#RUN  npm install /opt/etherpad-lite/temp/ep_pogs/

# Copy the custom configuration file, if present. The configuration file has to
# be manually put inside the same directory containing the Dockerfile (we cannot
# directly point to "../settings.json" for Docker's security restrictions).
#
# For the conditional COPY trick, see:
#   https://stackoverflow.com/questions/31528384/conditional-copy-add-in-dockerfile#46801962
COPY settings.json /opt/etherpad-lite/
COPY APIKEY.txt /opt/etherpad-lite/
COPY pad.css /opt/etherpad-lite/src/static/custom/
COPY APIAddedFunctions.js /opt/etherpad-lite/temp/
COPY injectChangesToAPICode.sh /opt/etherpad-lite/temp/
RUN /opt/etherpad-lite/temp/injectChangesToAPICode.sh


# Fix group permissions
RUN chmod -R g=u .

USER root
RUN cd src && npm link
USER etherpad

HEALTHCHECK --interval=20s --timeout=3s CMD ["etherpad-healthcheck"]

EXPOSE 9001
CMD ["etherpad"]
