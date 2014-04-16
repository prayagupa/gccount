import eccount.config.AbstractConfManager

// Place your Spring DSL code here
beans = {

    confManager(AbstractConfManager){ bean->

        q="" //qualifier used for index in ES

    }

}
