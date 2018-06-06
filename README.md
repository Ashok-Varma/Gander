Gander
=====
#### Thanks to [Jeff Gilfelt](https://github.com/jgilfelt) for his amazing library [Chuck](https://github.com/jgilfelt/chuck). This repo is a fork from Chuck, later on moved and released as separate project, since chuck is no longer maintained.

Gander is a simple in-app HTTP inspector for Android OkHttp clients. Gander intercepts and persists all HTTP requests and responses inside your application, and provides a UI for inspecting their content.

![Gander](assets/gander.gif)

Apps using Gander will display a notification showing a summary of ongoing HTTP activity. Tapping on the notification launches the full Gander UI. Apps can optionally suppress the notification, and launch the Gander UI directly from within their own interface. HTTP interactions and their contents can be exported via a share intent.

The main Gander activity is launched in its own task, allowing it to be displayed alongside the host app UI using Android 7.x multi-window support.

![Multi-Window](assets/multiwindow.gif)

Gander requires Android 4.1+ and OkHttp 3.x.

**Warning**: The data generated and stored when using this interceptor may contain sensitive information such as Authorization or Cookie headers, and the contents of request and response bodies. It is intended for use during development, and not in release builds or other production deployments.

Setup
-----

Add the dependency in your `build.gradle` file. Add it alongside the `no-op` variant to isolate Gander from release builds as follows:

```gradle
 dependencies {
   debugCompile 'com.ashokvarma.android:gander:1.0.6.1'
   releaseCompile 'com.ashokvarma.android:gander-no-op:1.0.6.1'
 }
```

In your application code, create an instance of `GanderInterceptor` (you'll need to provide it with a `Context`, because Android) and add it as an interceptor when building your OkHttp client:

```java
OkHttpClient client = new OkHttpClient.Builder()
  .addInterceptor(new GanderInterceptor(context, true))
  .build();
```

That's it! Gander will now record all HTTP interactions made by your OkHttp client. You can optionally disable the notification by calling `showNotification(false)` on the interceptor instance, and launch the Gander UI directly within your app with the intent from `Gander.getLaunchIntent()`.

FAQ
---
- Why are some of my request headers missing?
- Why are retries and redirects not being captured discretely?
- Why are my encoded request/response bodies not appearing as plain text?

Please refer to [this section of the OkHttp wiki](https://github.com/square/okhttp/wiki/Interceptors#choosing-between-application-and-network-interceptors). You can choose to use Gander as either an application or network interceptor, depending on your requirements.

Diff between Chuck and Gander:
1. Gander uses Room for db instead of Cupboard
2. Removed Gson Dependency
3. Improved Search
4. Improved Performance (PagedList, BackGround Load for Text, other minor pref boosts)
5. Minor fixes (Notification Channel Creation ..etc)
6. Many new features (Search highlight, Improved Notifications ..etc)

Acknowledgements
----------------
Chuck (parent repo)
- [Chuck](https://github.com/jgilfelt/chuck) - Copyright Jeff Gilfelt, Inc.

Gander uses the following open source libraries:
- [OkHttp](https://github.com/square/okhttp) - Copyright Square, Inc.

License
-------

    Copyright (C) 2018 Ashok Varma.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
