FROM <<insert image name here that includes JDK8>> 

EXPOSE 8080

# add CD scripts 
COPY cd_*.sh /opt/
RUN chmod +x /opt/cd_*.sh

# Add app
COPY fe*.jar /opt/
CMD ["sh", "/opt/cd_run.sh"]

