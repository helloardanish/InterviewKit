# InterviewKit

> Crack the interview, one pattern at a time.

A curated, interview-ready knowledge base of Java solutions — LeetCode problems, design patterns, general coding challenges, and real company interview questions — all organized by pattern, explained with intuition, and ready to browse as a website.

---

## What's Inside

| Section | Description |
|---|---|
| LeetCode 150 | Problems organized by pattern (Array/String, Two Pointers, Sliding Window, Trees, Graphs, etc.) — each with intuition, complexity analysis, and clean Java code. |
| Design Patterns | Adapter, Builder, Decorator, Factory, Observer, Proxy, Singleton, Strategy, and more — implemented in idiomatic Java with real-world use cases. |
| General Coding Problems | Data structures from scratch, utility algorithms, and CS fundamentals. |
| Company Interview Questions | Real questions asked at FAANG and other companies, tagged by company and role. |

---

## Project Structure

```text
InterviewKit/
│
├── 01-Leetcode-150/
│   ├── 00-General/
│   │   ├── DFS.java
│   │   ├── GraphImpl.java
│   │   └── HashCode.java
│   │
│   ├── 01-Array-or-String/
│   ├── 02-Two-Pointers/
│   ├── 03-Sliding-Window/
│   ├── 04-Matrix/
│   ├── 05-Hashmap/
│   ├── 06-Intervals/
│   ├── 07-Stack/
│   ├── 08-Linked-List/
│   ├── 09-Binary-Tree-General/
│   ├── 10-Binary-Tree-BFS/
│   ├── 11-Binary-Search-Tree/
│   ├── 12-Graph-General/
│   ├── 13-Graph-BFS/
│   ├── 14-Trie/
│   ├── 15-Backtracking/
│   ├── 16-Divide-and-Conquer/
│   ├── 17-Kadane-Algorithm/
│   └── leetcode.md
│
├── 02-Design-Patterns/
│   ├── Adapter/
│   ├── Builder/
│   ├── Decorator/
│   ├── Facade/
│   ├── Factory/
│   ├── Observer/
│   ├── Proxy/
│   ├── Singleton/
│   ├── Strategy/
│   └── design-patterns.md
│
├── docs/
│   ├── TEMPLATE.md
│   └── commands.md
│
├── testing/
│
├── .github/
│   └── workflows/
│       ├── ci.yml
│       └── pages.yml
│
├── _config.yml
├── Gemfile
├── Gemfile.lock
├── index.md
├── README.md
└── LICENSE
```

---

## Problem File Format

Every problem follows a consistent template so it stays easy to scan and learn from.

### Example: 0001 — Two Sum

```markdown
# 0001 — Two Sum

## Problem
...

## Intuition
...

## Approach
...

## Complexity
- Time: O(n)
- Space: O(n)

## Java Solution
(Java code block)

## Dry Run
...

## Edge Cases
...

## Interview Takeaway
...
```

---

## Features

- **Pattern-based organization** — learn why, not just how.
- **Consistent problem template** — intuition, approach, code, and edge cases.
- **Browsable documentation website** — powered by Jekyll and Just the Docs.
- **Company interview preparation** — organize questions by company and role.
- **Design patterns alongside algorithms** — understand practical object-oriented design.
- **Full-text search** — quickly find problems and concepts.
- **Java implementations** — clean, readable, interview-ready solutions.
- **GitHub Pages deployment** — automatically build and publish documentation.

---

## Live Website

Explore the complete documentation:

**[InterviewKit — Live Website](https://helloardanish.github.io/InterviewKit/)**

The website is powered by Jekyll and the Just the Docs theme.

### Run Locally

Clone the repository:

```bash
git clone https://github.com/helloardanish/InterviewKit.git

cd InterviewKit
```

Install dependencies:

```bash
bundle install
```

Start the Jekyll development server:

```bash
bundle exec jekyll serve
```

Open:

```text
http://127.0.0.1:4000/InterviewKit/
```

To generate the production website:

```bash
JEKYLL_ENV=production bundle exec jekyll build
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Programming Language | Java 25+ |
| Documentation | Markdown |
| Static Site Generator | Jekyll |
| Documentation Theme | Just the Docs |
| Styling | SCSS / CSS |
| Dependency Management | Ruby Bundler |
| Hosting | GitHub Pages |
| CI/CD | GitHub Actions |
| Search | Just the Docs Search |

---

## Contributing

Contributions are welcome!

1. Fork the repository.
2. Pick a problem or design pattern to contribute.
3. Follow the documentation template in `docs/TEMPLATE.md`.
4. Write clean, readable Java implementations.
5. Include complexity analysis and edge cases.
6. Open a Pull Request.

Please follow the existing folder structure and documentation conventions when contributing.

---

## License

MIT — free to use, fork, modify, and learn from.

See [LICENSE](LICENSE) for details.

---

## Support

If this kit helps you prepare for interviews, give the repository a ⭐ on GitHub. It helps others discover it too.

> The best time to start preparing was 6 months ago. The second best time is now.