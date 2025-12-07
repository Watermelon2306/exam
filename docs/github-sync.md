# Syncing this project to GitHub

Use the steps below to push the local `work` branch to your GitHub repository and open a pull request.

## 1. Configure your GitHub remote

Replace the URL with your destination repository:

```bash
git remote add origin git@github.com:<your-org>/<your-repo>.git
# If origin already exists, update it instead
# git remote set-url origin git@github.com:<your-org>/<your-repo>.git
```

## 2. Fetch and rebase to keep history clean

```bash
git fetch origin
git rebase origin/main  # or your default branch
```

If rebase reports conflicts, resolve them locally before continuing.

## 3. Push the branch

```bash
git push -u origin work
```

If your repository requires HTTPS, replace the SSH URL accordingly.

## 4. Open a pull request

On GitHub, create a PR from `work` into your default branch. Include:

- A clear title (e.g., "Create exam system backend skeleton")
- A summary of the modules and services added
- Any testing notes

## 5. Keep the PR updated

After additional commits, push them with `git push`. GitHub will update the PR automatically.

## 6. Merge and cleanup

Once approved, merge via GitHub. Optionally delete the `work` branch both remotely and locally:

```bash
git push origin --delete work
git checkout main
git branch -D work
```
