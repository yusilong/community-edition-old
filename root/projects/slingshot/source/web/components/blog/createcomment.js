/**
 * CreateComment component.
 * 
 * @namespace Alfresco
 * @class Alfresco.CreateComment
 */
(function()
{
   /**
    * Comment constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.CreateComment} The new CreateComment instance
    * @constructor
    */
   Alfresco.CreateComment = function(htmlId)
   {
      this.name = "Alfresco.CreateComment";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["event", "editor"], this.onComponentsLoaded, this);
      
      return this;
   }
   
   Alfresco.CreateComment.prototype =
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
      },
      
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function CreateComment_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      setMessages: function CreateComment_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },
       
      
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function CreateComment_onComponentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function CreateComment_onReady()
      { 
         this.registerCreateCommentForm(); 
      },
      
      /**
       * Registers the form with the html (that should be available in the page)
       * as well as the buttons that are part of the form.
       */
      registerCreateCommentForm: function CreateComment_registerCreateCommentForm()
      {
         // register the okButton
         var okButton = new YAHOO.widget.Button(this.id + "-createcomment-ok-button", {type: "submit"});
         
         // instantiate the simple editor we use for the form
         this.editor = new YAHOO.widget.SimpleEditor(this.id + '-createcomment-content', {
            height: '250px',
            width: '538px',
            dompath: false, //Turns on the bar at the bottom
            animate: false, //Animates the opening, closing and moving of Editor windows
            markup: "xhtml",
            toolbar:  Alfresco.util.editor.getTextOnlyToolbarConfig(this._msg)
         });
         this.editor.render();
         
         // create the form that does the validation/submit
         var commentForm = new Alfresco.forms.Form(this.id + "-createcomment-form");
         commentForm.setShowSubmitStateDynamically(true, false);
         commentForm.setSubmitElements(okButton);
         commentForm.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onCreateFormSubmitSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: this.onCreateFormSubmitFailure,
               scope: this
            }
         });
         commentForm.setSubmitAsJSON(true);
         commentForm.doBeforeFormSubmit =
         {
            fn: function(form, obj)
            {
               //Put the HTML back into the text area
               this.editor.saveHTML();
            },
            scope: this
         }
         
         commentForm.init();
      },     
      
      /**
       * Called when the form has been successfully submitted.
       */
      onCreateFormSubmitSuccess: function CreateComment_onCreateFormSubmitSuccess(response, object)
      {
         if (response.json.error != undefined)
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("comments.msg.unableCreateComment", response.json.error)});
         }
         else
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("comments.msg.commentCreated")});
            location.reload(true);
         }

      },
      
      /** Called when the form submit failed. */
      onCreateFormSubmitFailure: function CreateComment_onCreateFormSubmitFailure(response)
      {
         Alfresco.util.PopupManager.displayMessage({text: this._msg("comments.msg.failedCreateComment")});
      },

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function CreateComment_msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.CreateComment", Array.prototype.slice.call(arguments).slice(1));
      }
   };
})();
