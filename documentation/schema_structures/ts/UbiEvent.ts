import { integer } from "@opensearch-project/opensearch/api/types";

/**
 * Ubi Event data structures
 */

export class UbiObject {
	public readonly object_type:string;
	public internal_id:string;
	public object_id:string;
	public description:string|null;
	public object_details:string|object|null;
	constructor(type:string, id:string, description:string|null=null, details:string|object|null=null) {
		this.object_type = type;
		this.internal_id = id;
		this.description = description;

		if(typeof details == 'object'){
			this.object_details = {'json': details};
		} else {
			this.object_details = details;
		}

		//override if using key_field's and values
		this.object_id = id;
	}
}
export class UbiPosition{
	public ordinal:integer|null=null;
	public x:integer|null=null;
	public y:integer|null=null;
	public trail:string|null=null;

	constructor({ordinal=null, x=null, y=null, trail=null}={}) {
		this.ordinal = ordinal;
		this.x = x;
		this.y = y;
		this.trail = trail;
	}
}


export class UbiEventAttributes {
	/**
	 * Attributes, other than `object` or `position` should be in the form of a dictionary
	 * attributes['item1'] = 1
	 * attributes['item2'] = '2'
	 * attributes['user'] = User123
	 * 
	 * The object member is reserved for further, relevant object payloads or classes
	 */
	public object:UbiObject|null=null; 		//any data object
	public position:UbiPosition|null = null;	//click or other location
	constructor(object:UbiObject|null=null, position:UbiPosition|null=null) {
		this.object = object;
		this.position = position;
	}
}



export class UbiEvent {
	/**
	 * The following are keywords for the logging schema
	 * All other event attributes should be set in this.event_attributes
	 */
	public readonly application:string;	//name of the application log
	public readonly action_name:string;	//name of the javascript event
	public readonly user_id:string;		//user causing the event
	public query_id:string;				//associated query id if there are any
	public session_id:string;
	public message_type:string='INFO';
	public message:string;
	public page_id:string= window.location.pathname
	public timestamp:number=Date.now();

	/**
	 * All other event attribures should be assigned to this structure
	 */
	public event_attributes:UbiEventAttributes = new UbiEventAttributes();

	constructor(application:string, action_name:string, user_id:string, query_id:string, message:string|null=null) {
		this.application = application;
		this.action_name = action_name;
		this.user_id = user_id;
		this.query_id = query_id;

		if( message )
			this.message = message;
	}

	setMessage(message:string, message_type:string='INFO'){
		this.message = message
		this.message_type = message_type
	}

	/**
	 * Use to suppress null objects in the json output
	 * @param key 
	 * @param value 
	 * @returns 
	 */
	static replacer(key, value){
		if(value == null)
			return undefined;
		return value;
	}

	/**
	 * 
	 * @returns json string
	 */
	toJson():string {
		return JSON.stringify(this, UbiEvent.replacer);
	}
}
