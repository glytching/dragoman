Let's walk through an example.

The data returned from this call to GitHub's public API: https://api.github.com/events look like (abbreviated for readability) ...

```
[
    {
        "id": "6800330539",
        "type": "PushEvent",
        "actor": {
            "id": 265963,
            "login": "jbuget",
            "display_login": "jbuget",
            "gravatar_id": "",
            "url": "https://api.github.com/users/jbuget",
            "avatar_url": "https://avatars.githubusercontent.com/u/265963?"
        },
        "repo": {
            "id": 63066169,
            "name": "sgmap/pix",
            "url": "https://api.github.com/repos/sgmap/pix"
        },
        "payload": {
            "push_id": 2095289666,
            "size": 1,
            "distinct_size": 1,
            "ref": "refs/heads/gh-pages",
            "head": "6b125cdb92591ee2f127e819e8d46b8da3357e36",
            "before": "2c7174c97fe3ed58e61cf1c6a1dd9fbf4a89256d",
            "commits": [
                {
                    "sha": "6b125cdb92591ee2f127e819e8d46b8da3357e36",
                    "author": {
                        "email": "jbuget@gmail.com",
                        "name": "Jérémy Buget"
                    },
                    "message": "Release of dev with env integration (via commit hash: 35b89d37577758b268e93ff970d10d620f6618b5)",
                    "distinct": true,
                    "url": "https://api.github.com/repos/sgmap/pix/commits/6b125cdb92591ee2f127e819e8d46b8da3357e36"
                }
            ]
        },
        "public": true,
        "created_at": "2017-11-02T13:39:56Z",
        "org": {
            "id": 7874148,
            "login": "sgmap",
            "gravatar_id": "",
            "url": "https://api.github.com/orgs/sgmap",
            "avatar_url": "https://avatars.githubusercontent.com/u/7874148?"
        }
    },
    {
      ....
    }
]
```

Dragoman allows the user to filter and project on this data using a SQL-esque grammar. For example, given the following ...

* Projections: `repo.name, created_at`
* Predicates: `type='PushEvent' and actor.login='jbuget'`

... Dragoman would return:

```
[
    {
        "repo": {
            "name": "sgmap/pix",
        },
        "created_at": "2017-11-02T13:39:56Z"
    },
    {
      ....
    }
]
```

So, what? Doesn't the Github Public API already support filtering and projecting? Well,  Dragoman provides these features in a manner which is independent of the underlying data provider and hence can be applied (in a consistent manner) to numerous disparate datasets.

In addition, the user can subscribe to changes in these datasets applying the user's chosen predicates and projections to the changes.
