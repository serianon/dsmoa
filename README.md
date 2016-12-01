# What is DSMoA

Data-Stream-Mining on Android (DSMoA) is a student-project. The goal is to make [Data-Stream-Mining](https://en.wikipedia.org/wiki/Data_stream_mining) available on Android (duh).

![DSM-Service-Monitor](https://github.com/serianon/dsmoa/blob/master/Screenshots/svc_main_a.png "DSM-Service-Monitor")

# State and scope of DSMoA

Please keep in mind that this project is a mere first step into that topic. I managed to build an android-service that runs in the background and provides Data-Stream-Mining-algorithms for other apps, called "DSM-Service". You can think of this DSM-Service as somewhat similar to the Google Play Services. Apps can connect to this service and register for updates. The service delivers new information to the apps (observer-pattern). For the communication I use Androids IPC with AIDL.

The service contains an external DSM-library, namely [Massive Online Analysis](http://moa.cms.waikato.ac.nz/) (MOA). I used [this](https://github.com/mtraton/Android-Massive-Online-Analysis/) port for DSMoA. After weeks and weeks of work, I found out that this port must be buggy. The clustering-algorithms don't produce any cluster even when they certainly should. On the other hand, the MOA-port doesn't even produce the same sample-results as the MOA-standalone when running on Android, even if the parameters are identical. So MOA is a mere placeholder in this project. I designed the interfaces to the DSM so that the specific implementation (here: MOA) can be easily replaced. If you want to see use-cases, you'd need to find a replacement for MOA first. I'd recommend implementing some DSM-algorithms in java by yourself instead of relying on external librarys, just to be sure.

I used a sample use-case for DSMoA. But now that you know that the used MOA-port is buggy, you know that there can't be useful results. But nevertheless, the idea was that DSM-cluster-algorithms like denstream (with DBScan as offline component) produce clusters out of GPS points the user generates over time. Then you know where the user stays for periods of time, like at home or at work, bus stop and so on. A second step could be, that one uses hoeffding-trees and some more data, to predict to which cluster the user is moving in the future. With this functionality in the DSM-Service, you could start to implement apps that, for instance, show you when your bus is coming even before you reach the stop and even before the user reaches out for is smartphone to look it up. Think of this as Google Now, but cooler ;).

There is more to this DSMoA project. I took care of some debugging features, especially visualizing the clusters. With MOA, the models from the DSM-algorithms can be persisted in storage. All in all the DSM-service should be relatively robust when it comes to forced closing and reboots in general. Pulling GPS-fixes is slightly optimized for low power consumption and offering two configurations which you can choose from in the settings (three if you count the debug-settings). There is a debug-mode where you can manually invoke callbacks (from the service to the apps) whenever you want, and so-called moa-self-tests, mostly to prove that the MOA-port is somewhat working but producing result which are bonkers. The communication between app and service uses AIDL, like I already explained. To make this more comfortable I packed all this stuff in a neat and easy to use library and added a sample-app which shows how to implement said library. And there is still much more in here, but I will skip over all the details for now :).

# Where to go from here

First of all, I never managed to get as far as I really wished I could. I'd especially like to see:
* Working DSM-libary or DSM-algorithms in DSMoA
* Then a working real-world example
* Evaluation: Is DSM suitable for machine-learning on the device only? How is the energy consumption and the quality of the models? Is it useful? Can it replace Client-Server structure and therefore improve privacy?

I made this code public; maybe someone is interested in bringing this project to the next level. But most of all, I wanted to share what I could find out especially when it comes to IPC with AIDL and MOA's Android-port, because these two topics included figuring out a lot of things by myself, since the documentation and examples I could find online were just not very useful. If there is someone out there struggeling with one of these topics, I'm glad to show you some of my results. Happy Coding!
