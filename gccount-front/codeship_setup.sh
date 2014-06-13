
###########################
#codeship deployment
###########################

wget http://dist.springframework.org.s3.amazonaws.com/release/GRAILS/grails-2.4.0.zip
unzip grails-2.4.0.zip -d ~/bin
chmod -R 777 ~/bin/grails-2.4.0
echo "GRAILS_HOME=~/bin/grails-2.4.0" > ~/.bash_profile
export GRAILS_HOME=~/bin/grails-2.4.0
echo "PATH=$PATH:$GRAILS_HOME/bin" > ~/.bash_profile
export PATH=$PATH:$GRAILS_HOME/bin
source ~/.bash_profile
