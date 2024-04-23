The datastructures that we use for indexing events adhere to the following nested structure that aligns with the [schemas](.././schemas.md):

`struct UbiEvent {`
- application
- action_name
- user_id
- query_id
- session_id
- page_id
- message_type
- timestamp
- <details open>
	<summary>event_attributes {</summary>
	<p>

  - <details>
  	<summary>position {</summary>

  		- ordinal
  		- x
  		- y
  		- trail
  	}
  	</details>
  - <details>
  	<summary>object {</summary>

  		- internal_id
  		- object_id
  		- object_type
  		- description
  		- object_details /
        - object_details.json
  		}
  	</details>
	}
  </details>}
`}`
