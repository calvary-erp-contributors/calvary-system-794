import './home.scss';

import React from 'react';
import { Link } from 'react-router-dom';

import { Row, Col, Alert } from 'reactstrap';

import { useAppSelector } from 'app/config/store';

export const Home = () => {
  const account = useAppSelector(state => state.authentication.account);

  return (
    <Row>
      <Col md="3" className="pad">
        <span className="hipster rounded" />
      </Col>
      <Col md="9">
        <h2>
          Calvary ERP
        </h2>
        <p className="lead">
          Church data management system
        </p>
        {account?.login ? (
          <div>
            <Alert color="success">You are logged in as user &quot;{account.login}&quot;.</Alert>
          </div>
        ) : (
          <div>
            <Alert color="warning">
              <span>&nbsp;</span>
              <Link to="/login" className="alert-link">
                sign in
              </Link>
            </Alert>

            <Alert color="warning">
              You don&apos;t have an account yet?&nbsp;
              <Link to="/account/register" className="alert-link">
                Register a new account
              </Link>
            </Alert>
          </div>
        )}
        <p>This system is developed on JHipster. If you have any question on JHipster find us on:</p>

        <ul>
          <li>
            <a href="https://www.jhipster.tech/" target="_blank" rel="noopener noreferrer">
              JHipster homepage
            </a>
          </li>
          <li>
            <a href="https://stackoverflow.com/tags/jhipster/info" target="_blank" rel="noopener noreferrer">
              JHipster on Stack Overflow
            </a>
          </li>
          <li>
            <a href="https://github.com/jhipster/generator-jhipster/issues?state=open" target="_blank" rel="noopener noreferrer">
              JHipster bug tracker
            </a>
          </li>
          <li>
            <a href="https://gitter.im/jhipster/generator-jhipster" target="_blank" rel="noopener noreferrer">
              JHipster public chat room
            </a>
          </li>
          <li>
            <a href="https://twitter.com/jhipster" target="_blank" rel="noopener noreferrer">
              follow @jhipster on Twitter
            </a>
          </li>
        </ul>

        <p>
          If you like JHipster, don&apos;t forget to give us a star on{' '}
          <a href="https://github.com/jhipster/generator-jhipster" target="_blank" rel="noopener noreferrer">
            GitHub
          </a>
          !
        </p>
      </Col>
    </Row>
  );
};

export default Home;
