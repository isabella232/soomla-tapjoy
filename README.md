soomla-tapjoy
=============

This project is a plugin for The SOOMLA Project's stores. It currently supports (android-store)[http://github.com/soomla/android-store]..

There's an IntelliJ example added this this project. You can see the the way we call Tapjoy in (StoreGoodsActivity.java)[https://github.com/refaelos/soomla-tapjoy/blob/master/example/soomla-tapjoy/src/com/soomla/tapjoy/example/StoreGoodsActivity.java].

Getting Started
---

1. Copy the file SoomlaTapjoy.java to your project and place it in: src/com/soomla/tapjoy.
2. Include the jar 'tapjoyconnectlibrary.jar' in your project's classpath.
3. Go to the Tapjoy console and set the currecy name to be the same as the itemId of your game's currency.
4. In order to update the currency balance after TapPoints were given to the user, override 'onResume()' and in that function call:

   ```Java
   SoomlaTapjoy.getInstance().update();
   ```

5. When you want to open Tapjoy's Offerwall call:
    
    ```Java
    SoomlaTapjoy.getInstance().showOfferWall();
    ```

Updating currency balance in the UI
---

When you call 'SoomlaTapjoy.getInstance().update();' (step 3 above) the plugin will update the currency balance in the database.  
If you want to update the UI when the currency balance is updated, you'll need to have your activity implement IStoreEventHandler and Added to StoreEventHandlers.

Add your Activity to StoreEventHandlers in 'onResume()' and remove it in 'onPause()'. That way you make sure your Activity is added properly to handle events.

You can update the UI when 'currencyBalanceChanged' is fired.

SOOMLA, Elsewhere ...
---

+ [Website](http://project.soom.la/)
+ [On Facebook](https://www.facebook.com/pages/The-SOOMLA-Project/389643294427376).
+ [On AngelList](https://angel.co/the-soomla-project)

License
---
MIT License. Copyright (c) 2012 SOOMLA. http://project.soom.la
+ http://www.opensource.org/licenses/MIT
