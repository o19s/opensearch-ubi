The datastructures that we use for indexing events adhere to the following nested structure that aligns with the Ubi schemas.  See the [schemas](.././schemas.md) for descriptions and examples of the following fields.

`struct UbiEvent {`
- application
- action_name
- user_id
- query_id
- session_id
- page_id
- message_type
- timestamp
- <details>
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

Typescript versions of these classes can be found in [ts/UbiEvent.ts](./ts/UbiEvent.ts).

Example javascript code:
```js
    // basic message format
    let e = new UbiEvent('add_to_cart', user_id, query_id);
    e.message_type = 'PURCHASE';
    e.message = item.title + ' (' + item.primary_ean + ')';
    e.session_id = session_id;
    e.page_id = window.location.pathname;

    // adding custom data fields
    e.event_attributes['user_info'] = {user_name:'TheJackal', phone:'555-555-1234'}

    // associate the object added to the cart
    e.event_attributes.object = new UbiObject('product', item.primary_ean, item.title, item);

    // save any click info
    e.event_attributes.position = new UbiPosition({x:event.clientX, y:event.clientY});
    
    // index here
    console.log(e.toJson());

```
