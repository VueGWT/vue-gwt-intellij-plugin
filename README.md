# Vue GWT IntelliJ Plugin

Add support for [Vue GWT](https://github.com/Axellience/vue-gwt) in IntelliJ.

When you change your Vue Components templates this plugin will automatically touch the associated Java file.


By default IntelliJ doesn't support automatic annotation processing on Java file change when the app is running.

After installing this plugin, follow these steps to enable it:


Go to
`File > Settings > Build, Execution, Deployment > Compiler` and enable "Make project automatically"
<br/><br/>

Open the Action window :
    
* Linux : `CTRL+SHIFT+A`
* MacOS : `SHIFT+COMMAND+A`
* Windows : `CTRL+ALT+SHIFT+/`
</ul>

Enter `Registry...` and enable `compiler.automake.allow.when.app.running`.


You are good to go!