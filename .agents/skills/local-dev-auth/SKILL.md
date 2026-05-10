---
name: local-dev-auth
description: Use when local API testing needs a dev temp access token from the repository's temporary auth endpoint and code-defined test credentials.
---

# Local Dev Auth

Use this skill to get a local dev access token for authenticated API tests.

## Do

- Read the auth controller or nearby code to confirm the dev temp token endpoint and required request body.
- Request a temp ATK using the code-defined dev credentials.
- Use a suitable local test member ID.
- Return the token in a ready-to-use Authorization header form.

## Rules

- Prefer the repository's built-in temp auth flow over handcrafted tokens.
- Re-check the code if the endpoint or credentials may have changed.
- Do not assume a member ID without confirming available local test data when needed.

## Output

Provide:

- the token value
- the header form: `AUTHORIZATION: Bearer <token>`
- the member used for the test
