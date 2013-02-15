package com.vtence.molecule;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.internal.State;
import org.jmock.internal.StateMachine;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.vtence.molecule.support.MockRequest;
import com.vtence.molecule.support.MockResponse;

@RunWith(JMock.class)
public class MiddlewareStackTest {

    MiddlewareStack stack = new MiddlewareStack();

    Mockery context = new JUnit4Mockery();
    Application application = context.mock(Application.class);
    Middleware onTop = context.mock(Middleware.class, "on top");
    Middleware inTheMiddle = context.mock(Middleware.class, "in the middle");
    Middleware atBottom = context.mock(Middleware.class, "at bottom");

    Request request = new MockRequest();
    Response response = new MockResponse();
    final States chain = new StateMachine("chain");

    @Test public void
    assemblesChainInOrderOfAddition() throws Exception {
        expectMiddlewaresToBeChainedFromTopToBottomThen(chain.is("assembled"));
        expectChainToHandleRequestWhen(chain.is("assembled"));

        stack.use(onTop);
        stack.use(inTheMiddle);
        stack.use(atBottom);
        stack.run(application);

        stack.handle(request, response);
    }

    private void expectMiddlewaresToBeChainedFromTopToBottomThen(final State state) {
        context.checking(new Expectations() {{
            oneOf(onTop).connectTo(inTheMiddle);
            oneOf(inTheMiddle).connectTo(atBottom);
            oneOf(atBottom).connectTo(application); then(state);
        }});
    }

    private void expectChainToHandleRequestWhen(final State state) throws Exception {
        context.checking(new Expectations() {{
            oneOf(onTop).handle(with(same(request)), with(same(response))); when(state);
        }});
    }
}
