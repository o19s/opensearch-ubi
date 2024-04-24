import json
import random
import uuid
import datetime
import time
from typing import Union
from opensearchpy import Document


def unix_now()->float:
    return time.mktime(datetime.datetime.now().timetuple())

def guuid()->str:
    return str(uuid.uuid4())

class JsonDocument(Document):
	def __init__(self, **kwargs) -> None:
		super().__init__(**kwargs)
	def __str__(self) -> str:
		d = self.to_dict()
		if 'index_name' in d:
			del d['index_name']
		return str(d)

	def to_json(self) -> str:
		d = self.to_dict()
		#index name isn't part of the schema
		if 'index_name' in d:
			del d['index_name']
		return json.dumps(d)

class UbiPosition(JsonDocument):
	def __init__(self, **kwargs) -> None:
		super().__init__(ordinal:int|None=None,x:int|None=None, y:int|None=None,trail:str|None=None,   **kwargs)
		self.ordinal:int|None=None
		self.x:int|None=None
		self.y:int|None=None
		self.trail:str|None=None

class UbiObject(JsonDocument):
	def __init__(self, object_type:str, object_id:str, description:str=None, details:Union[Document,str]=None, **kwargs) -> None:
		super().__init__(**kwargs)
		self.object_type:str = object_type
		self.internal_id:str = object_id
		self.object_id:str = object_id
		self.description:str = description
		if details:
			if isinstance(details, JsonDocument) or isinstance(details, Document):
				self.object_detail:dict = details.to_dict()
			elif not isinstance(details, dict):
				self.object_detail:str = str(details)

class UbiEventAttributes(JsonDocument):
	def __init__(self, object:UbiObject=None, position:dict=None,  **kwargs) -> None:
		super().__init__(**kwargs)
		self.position:dict = position if position else {}
		if object:
			if isinstance(object, JsonDocument):
				self.object:dict = object.to_dict()
			elif not isinstance(object, dict):
				print(f'WARNING: Unknown data type->{type(object)}')
				self.object:dict = {'value':object}
			else:
				self.object:dict = object

	def to_dict(self)-> dict:
		d = super().to_dict()
		for key in d.keys():
			if hasattr(d[key], 'to_dict'):
				d[key] = d[key].to_dict()
		return d

class UbiEvent(JsonDocument):
	def __init__(self, application, action_name, user_id, query_id=None, session_id:str=None, message=None, object=None, **kwargs) -> None:
		super().__init__(**kwargs)
		self.application:str = application
		self.action_name:str = action_name
		self.user_id:str = user_id
		self.query_id:str = query_id
		self.session_id:str = session_id
		self.message:str = message
		self.message_type:str = 'INFO'
		self.timestamp = unix_now()
		if object:
			self.event_attributes:UbiEventAttributes = UbiEventAttributes(object=object)
		else:
			self.event_attributes:UbiEventAttributes = None

	def has_position(self):
		return self.event_attributes and self.event_attributes.position

	def set_position(self, position):
		if not self.event_attributes:
			self.event_attributes = UbiEventAttributes(position=position)
		else:
			self.event_attributes.position = position
	  
	def to_dict(self) -> dict:
		d = super().to_dict()
		for key in d.keys():
			if hasattr(d[key], 'to_dict'):
				d[key] = d[key].to_dict()
		return d

if __name__ == '__main__':
	e = UbiEvent(application='chorus', action_name='add_to_cart', 
              user_id='USER-' + guuid(), query_id='QUERY-ID-'+ guuid(), session_id='SESSION-' + guuid(),
              # object added to the cart
              object= UbiObject(
                  		object_type='product', 
                    	object_id='ISBN-' + guuid(), 
                      description='a very nice book', details={'product_data':'random product data'}              
            )
	)
	e.message_type = 'PURCHASE'
	
	# adding custom attribute fields
	e.event_attributes['user_info'] = {'user_name':'TheJackal', 'phone':'555-555-1234'}
 
 	# product position information
	e.event_attributes.position = UbiPosition(ordinal=3, x=250, y=400)

	print(e.to_json())