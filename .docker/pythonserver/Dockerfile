# Use an official Python runtime as a parent image
FROM python:2.7-slim

# Set the working directory to /app
WORKDIR /ScoringServer

# Copy the current directory contents into the container at /app
ADD ./others/ScoringServer /ScoringServer

# replace shell with bash so we can source files
RUN rm /bin/sh && ln -s /bin/bash /bin/sh

# Install any needed packages specified in requirements.txt


#RUN pip install mod_wsgi
RUN pip install fabric
RUN pip install pillow
RUN pip install numpy


#USER node
#RUN chown /app/etherpad-lite node:node

# Make port 80 available to the world outside this container
EXPOSE 8082

# Run app.py when the container launches
WORKDIR "/ScoringServer"

CMD ["python", "server.py"]