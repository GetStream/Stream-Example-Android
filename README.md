# Example Android photo sharing app

The application mimics an Instagram-style application where users can take photos, share them publicly,
follow other users, see a notification feed and aggregated data along the way. For more information
please check out our [blog post]().

We're covering some "best practice" examples of how to set up a mobile application powered with Stream
APIs, and we're happy to announce this Android example to share. As with all of our example code,
the project is open sourced and available on GitHub. We have also submitted the application to the
[Google Play Store](https://play.google.com/store/apps/details?id=io.getstream.example) for ease of installation.

## More information on Best Practices

You can read more about the Android client, the Go service that powers it, and our recommendations
for feed setup [on our blog](http://blog.getstream.io/category/best-practices/)

## Goals and Anti-Goals of this App

Our primary goal of this Android app was to showcase our best practices for communicating a mobile
app and a backend service. We do want to address several things up front about building a mobile
application with Stream. Most importantly, **we do not recommend that you build an application where
your API key and secret are embedded within or otherwise accessible to the mobile application** --
our best practice is that your mobile application communicate with a fast backend API hosted on your
own systems, and that this backend application would communicate with Stream.

This Android shows how to perform asynchronous HTTP operations to fetch various kinds of feeds and
present that data to your users. In the interest of time to develop this project, we took several
shortcuts along the way:
- Our mobile application does not perform secure communication with the backend process; please
incorporate secure best practices for something like JWT over SSL between your mobile app and your
backend.
- We took a shortcut on authentication using only usernames and email addresses; again, please
follow security best practices around authentication.
- We do not utilize feed filters to only pull the latest data; we do recommend that feed data be
cached on the mobile device and refreshed using the newest/oldest activity identifier to minimize
data transfer on mobile devices.
- There may be optimizations in how the Android application operates, manages photos and
orientation, and we would be happy to review ideas and pull requests from our community.
