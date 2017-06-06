package com.fortify.processrunner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fortify.processrunner.context.Context;
import com.fortify.processrunner.context.ContextPropertyDefinitions;
import com.fortify.processrunner.context.IContextPropertyDefinitionProvider;
import com.fortify.processrunner.processor.IProcessor;
import com.fortify.processrunner.processor.IProcessor.Phase;

/**
 * This class allows for running one or more independent {@link IProcessor}
 * implementations as configured through the {@link #setProcessors(IProcessor...)}
 * method.
 * 
 * @author Ruud Senden
 *
 */
public class ProcessRunner implements IContextPropertyDefinitionProvider {
	private static final Log LOG = LogFactory.getLog(ProcessRunner.class);
	private IProcessor[] processors = new IProcessor[]{};
	private String description;
	private boolean enabled = true;
	private boolean _default= false;

	/**
	 * Allow all configured {@link IProcessor} implementations to add
	 * their {@link ContextPropertyDefinitions}
	 */
	public void addContextPropertyDefinitions(ContextPropertyDefinitions contextPropertyDefinitions, Context context) {
		for ( IProcessor processor : processors ) {
			processor.addContextPropertyDefinitions(contextPropertyDefinitions, context);
		}
	}
	
	/**
	 * Run the configured processor(s) using the configured context.
	 * Each configured processor is run with its own individual context.
	 */
	public void run(Context context) {
		IProcessor[] processors = getProcessors();
		for ( IProcessor processor : processors ) {
			// Create a copy to let each processor have its own independent context
			Context processorContext = new Context(context);
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("[Process] Running processor "+processor+" with context "+context);
			}
			if ( process(Phase.PRE_PROCESS, processorContext, processor) ) {
				if ( process(Phase.PROCESS, processorContext, processor) ) {
					process(Phase.POST_PROCESS, processorContext, processor);
				}
			}
		}
	}

	/**
	 * Run the given phase with the given context on the given processor.
	 * @param phase
	 * @param context
	 * @param processor
	 * @return
	 */
	private static final boolean process(Phase phase, Context context, IProcessor processor) {
		LOG.debug("[Process] Running phase "+phase);
		boolean result = processor.process(phase, context);
		LOG.debug("[Process] Phase "+phase+" result: "+result);
		return result;
	}

	public IProcessor[] getProcessors() {
		return processors;
	}

	public void setProcessors(IProcessor... processors) {
		this.processors = processors;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isDefault() {
		return _default;
	}

	public void setDefault(boolean _default) {
		this._default = _default;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
