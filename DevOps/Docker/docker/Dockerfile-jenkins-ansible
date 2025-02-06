FROM jenkins/jenkins

USER root

RUN curl -O https://bootstrap.pypa.io/get-pip.py && \
    python get-pip.py && \
    pip install ansible --upgrade

USER jenkins