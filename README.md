# Vue GWT IntelliJ Plugin

Add support for [Vue GWT](https://github.com/VueGWT/vue-gwt) in IntelliJ.

When you change your Vue Components templates this plugin will automatically touch the associated Java file.

The Vue GWT IntelliJ Plugin is published in the [IntelliJ Plugins Repository](https://plugins.jetbrains.com/plugin/10441-vue-gwt).
You can find it in your IDE in `Preferences > Plugins > Browse Repositories... > Vue GWT`.

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
