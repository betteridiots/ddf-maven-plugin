package org.betteridiots.maven.plugins.ddf;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.betteridiots.ssh.SshExecFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


/**
 * The config-ddf goal is used for passing configuration options into the ddf kernel
 */
@Mojo( name = "config-ddf", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST )
public class DDFConfigureMojo extends AbstractMojo
{

    /**
     * File containing DDF commands (optional)
     */
    @Parameter( property = "config-ddf.paramsFile")
    private String paramsFile;

    /**
     * Array of parameters to be inserted into ddf config command
     */
    @Parameter( property = "config-ddf.configs")
    private String[] configs;

    /**
     * Username credentials for accessing ddf
     */
    @Parameter( property = "config-ddf.user", defaultValue = "admin" )
    private String user;

    /**
     * Password for accessing the ddf
     */
    @Parameter( property = "config-ddf.password", defaultValue = "admin" )
    private String password;

    /**
     * Hostname or IP address of the ddf
     */
    @Parameter( property = "config-ddf.host", defaultValue = "localhost" )
    private String host;

    /**
     * SSH Port for the ddf
     */
    @Parameter( property = "config-ddf.port", defaultValue = "8101" )
    private int port;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        // Log Parameters
        getLog().info( "DDF Host is: " + host );
        getLog().info( "DDF Port is: " + port );
        getLog().info( "DDF user is: " + user );
        getLog().info( "DDF password is: " + password );
        getLog().info( "DDF parameter file is: " + paramsFile );

        // Initialize BufferedReader and commands ArrayList
        StringBuffer params = new StringBuffer();
        String line;
        
        if (paramsFile != null) {
            
            // Initialize FileReader
            FileReader fileReader = null;
            try {
                fileReader = new FileReader(paramsFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BufferedReader bufferedReader = new BufferedReader(fileReader);


            // Parse paramsFile into commands single string
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    params.append(line +"; ");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try
            {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
	}else{

	    for (String config : configs){
                params.append(config+"; ");
            }
        }

        // Disable strict host key checking and set auth method to password
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("PreferredAuthentications","password");

        // Build JSch Session

        SshExecFactory sef = new SshExecFactory();

        String commands = params.toString();

        sef.buildChannel(user, password, host, port, config, commands);
        
    }

}