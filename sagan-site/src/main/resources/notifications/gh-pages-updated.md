What's this?
-----------------------------------

Your project has a gh-pages branch that is probably (hopefully!) based on the shared
gh-pages project at #{[push][metadataRepository][url]}. Some new commits just got pushed to that
shared repo, and that means it's probably a good idea to sync yours up with it. Here's
what changed:

#{[commits]}

Getting up to date is pretty easy. Just do the following:


In case you need to do a fresh checkout
----------------------------------

    git clone git@github.com:spring-projects/#{[project].id}.git
    cd #{[project].id}


If you don't yet have the common gh-pages project as an upstream
----------------------------------

    git remote add gh-pages-upstream git@github.com:spring-projects/gh-pages.git


Merge the latest changes
----------------------------------
    git checkout gh-pages
    git pull --no-ff gh-pages-upstream gh-pages


Analyze changes
----------------------------------

 - Work out merge conflicts, if any
 - Commit changes as necessary
 - Take a look at the site locally (see the [readme][1] for instructions how)


Push
-----------------------------------

    git push origin gh-pages

... and you're done.


Where did this issue come from?
-----------------------------------

This issue is created by a webhook that's set up on the shared gh-pages project.
See the [readme][1] for more details.

[1]: https://github.com/spring-projects/gh-pages#readme