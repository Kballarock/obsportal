package by.obs.portal.validation.view;

import javax.validation.GroupSequence;

@GroupSequence(value = {ErrorSequence.First.class, ErrorSequence.Second.class, ErrorSequence.Third.class})
public interface ErrorSequence {

    interface First {
    }

    interface Second {
    }

    interface Third {
    }
}