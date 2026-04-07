-- Enums
CREATE TYPE account_status AS ENUM ('PENDING', 'ACTIVE', 'SUSPENDED', 'BANNED', 'DELETED');
CREATE TYPE member_type AS ENUM ('INDIVIDUAL', 'ORGANIZATION');
CREATE TYPE application_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED');
CREATE TYPE attachment_type AS ENUM ('ID_CARD', 'BUSINESS_LICENSE', 'PROOF_OF_ADDRESS', 'OTHER');
CREATE TYPE visibility_level AS ENUM ('PUBLIC', 'MEMBERS_ONLY', 'PRIVATE');
CREATE TYPE name_card_plan AS ENUM ('BASIC', 'PREMIUM', 'VIP');
CREATE TYPE point_direction AS ENUM ('EARN', 'SPEND', 'EXPIRE', 'ADJUST');
CREATE TYPE point_source_type AS ENUM ('REGISTRATION', 'EVENT_CHECKIN', 'REFERRAL', 'REWARD_REDEMPTION', 'ADMIN_ADJUST', 'OTHER');
CREATE TYPE reward_status AS ENUM ('ACTIVE', 'OUT_OF_STOCK', 'DISABLED');
CREATE TYPE redemption_status AS ENUM ('PENDING', 'APPROVED', 'FULFILLED', 'REJECTED', 'CANCELLED');
CREATE TYPE referral_status AS ENUM ('CLICKED', 'REGISTERED', 'QUALIFIED', 'REWARDED', 'EXPIRED');
CREATE TYPE event_status AS ENUM ('DRAFT', 'UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELLED');
CREATE TYPE registration_status AS ENUM ('REGISTERED', 'CONFIRMED', 'CANCELLED', 'WAITLIST');
CREATE TYPE certificate_status AS ENUM ('ACTIVE', 'EXPIRED', 'REVOKED');
CREATE TYPE notification_channel AS ENUM ('EMAIL', 'SMS', 'PUSH', 'IN_APP');
CREATE TYPE delivery_status AS ENUM ('PENDING', 'SENT', 'DELIVERED', 'FAILED', 'READ');

-- Các enum khác nếu cần thêm sau

-- ====================== Core ======================
CREATE TABLE account (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20) UNIQUE,
    password_hash TEXT NOT NULL,
    status account_status NOT NULL DEFAULT 'PENDING',
    is_activated BOOLEAN NOT NULL DEFAULT FALSE,
    last_login_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE role (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE account_role_map (
    account_id UUID REFERENCES account(id) ON DELETE CASCADE,
    role_id UUID REFERENCES role(id) ON DELETE CASCADE,
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (account_id, role_id)
);

-- ====================== Authentication ======================
CREATE TABLE email_verification_token (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID UNIQUE REFERENCES account(id) ON DELETE CASCADE,
    token VARCHAR(255) UNIQUE NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    verified_at TIMESTAMPTZ
);

CREATE TABLE password_reset_token (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID REFERENCES account(id) ON DELETE CASCADE,
    token VARCHAR(255) UNIQUE NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    used_at TIMESTAMPTZ
);

-- ====================== Membership Application ======================
CREATE TABLE membership_application (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID REFERENCES account(id) ON DELETE CASCADE,
    requested_member_type member_type NOT NULL,
    applicant_name VARCHAR(255) NOT NULL,
    applicant_email VARCHAR(255) NOT NULL,
    applicant_phone VARCHAR(20),
    status application_status NOT NULL DEFAULT 'PENDING',
    submitted_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    reviewed_at TIMESTAMPTZ,
    reviewed_by UUID REFERENCES account(id),
    reject_reason TEXT
);

CREATE TABLE membership_application_attachment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    application_id UUID REFERENCES membership_application(id) ON DELETE CASCADE,
    attachment_type attachment_type NOT NULL,
    file_url TEXT NOT NULL,
    uploaded_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ====================== Member Profile ======================
CREATE TABLE industry_group (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE member_profile (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID UNIQUE REFERENCES account(id) ON DELETE CASCADE,
    member_code VARCHAR(50) UNIQUE NOT NULL,
    profile_type member_type NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    avatar_url TEXT,
    contact_email VARCHAR(255),
    contact_phone VARCHAR(20),
    bio TEXT,
    public_visibility visibility_level NOT NULL DEFAULT 'MEMBERS_ONLY',
    current_tier_id UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    approved_at TIMESTAMPTZ
);

CREATE TABLE individual_profile (
    member_profile_id UUID PRIMARY KEY REFERENCES member_profile(id) ON DELETE CASCADE,
    full_name VARCHAR(255) NOT NULL,
    occupation VARCHAR(255),
    industry_group_id UUID REFERENCES industry_group(id),
    address_text TEXT
);

CREATE TABLE organization_profile (
    member_profile_id UUID PRIMARY KEY REFERENCES member_profile(id) ON DELETE CASCADE,
    organization_name VARCHAR(255) NOT NULL,
    legal_representative VARCHAR(255),
    industry_group_id UUID REFERENCES industry_group(id),
    website VARCHAR(255),
    product_service_summary TEXT,
    address_text TEXT
);

CREATE TABLE profile_attachment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    member_profile_id UUID REFERENCES member_profile(id) ON DELETE CASCADE,
    attachment_type attachment_type NOT NULL,
    file_url TEXT NOT NULL,
    is_public BOOLEAN NOT NULL DEFAULT FALSE,
    uploaded_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE digital_name_card (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    member_profile_id UUID UNIQUE REFERENCES member_profile(id) ON DELETE CASCADE,
    share_code VARCHAR(100) UNIQUE NOT NULL,
    share_url TEXT NOT NULL,
    qr_code_url TEXT,
    plan_type name_card_plan NOT NULL DEFAULT 'BASIC',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ====================== Membership Tier & Benefit ======================
CREATE TABLE membership_tier (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE tier_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    member_profile_id UUID REFERENCES member_profile(id) ON DELETE CASCADE,
    from_tier_id UUID REFERENCES membership_tier(id),
    to_tier_id UUID REFERENCES membership_tier(id) NOT NULL,
    changed_by UUID REFERENCES account(id),
    change_reason TEXT,
    changed_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE benefit (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE tier_benefit (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tier_id UUID REFERENCES membership_tier(id) ON DELETE CASCADE,
    benefit_id UUID REFERENCES benefit(id) ON DELETE CASCADE,
    display_order INT NOT NULL DEFAULT 0,
    UNIQUE(tier_id, benefit_id)
);

-- Add foreign key sau khi tạo bảng
ALTER TABLE member_profile 
    ADD CONSTRAINT fk_member_profile_current_tier 
    FOREIGN KEY (current_tier_id) REFERENCES membership_tier(id);

-- ====================== Point System ======================
CREATE TABLE point_wallet (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    member_profile_id UUID UNIQUE REFERENCES member_profile(id) ON DELETE CASCADE,
    current_balance INT NOT NULL DEFAULT 0,
    used_points INT NOT NULL DEFAULT 0,
    expiring_points INT NOT NULL DEFAULT 0,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE point_transaction (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    wallet_id UUID REFERENCES point_wallet(id) ON DELETE CASCADE,
    member_profile_id UUID REFERENCES member_profile(id) ON DELETE CASCADE,
    direction point_direction NOT NULL,
    source_type point_source_type NOT NULL,
    source_id UUID,
    points_delta INT NOT NULL,
    note TEXT,
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE reward (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    description TEXT,
    required_points INT NOT NULL,
    stock_qty INT NOT NULL DEFAULT 0,
    status reward_status NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE reward_redemption (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    member_profile_id UUID REFERENCES member_profile(id) ON DELETE CASCADE,
    reward_id UUID REFERENCES reward(id),
    required_points INT NOT NULL,
    status redemption_status NOT NULL DEFAULT 'PENDING',
    redeemed_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    approved_at TIMESTAMPTZ,
    approved_by UUID REFERENCES account(id),
    fulfilled_at TIMESTAMPTZ,
    rejected_reason TEXT
);

-- ====================== Referral ======================
CREATE TABLE referral_link (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    member_profile_id UUID REFERENCES member_profile(id) ON DELETE CASCADE,
    referral_code VARCHAR(50) UNIQUE NOT NULL,
    share_url TEXT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE referral_tracking (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    referral_link_id UUID REFERENCES referral_link(id),
    referrer_member_id UUID REFERENCES member_profile(id),
    referred_account_id UUID REFERENCES account(id),
    status referral_status NOT NULL DEFAULT 'CLICKED',
    clicked_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    registered_at TIMESTAMPTZ,
    qualified_at TIMESTAMPTZ,
    rewarded_at TIMESTAMPTZ
);

-- ====================== Event ======================
CREATE TABLE event_type (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE event (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    event_type_id UUID REFERENCES event_type(id),
    location_text TEXT,
    start_at TIMESTAMPTZ NOT NULL,
    end_at TIMESTAMPTZ NOT NULL,
    max_capacity INT,
    status event_status NOT NULL DEFAULT 'UPCOMING'
);

CREATE TABLE event_registration (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id UUID REFERENCES event(id) ON DELETE CASCADE,
    member_profile_id UUID REFERENCES member_profile(id) ON DELETE CASCADE,
    registration_status registration_status NOT NULL DEFAULT 'REGISTERED',
    qr_code_value VARCHAR(255) UNIQUE,
    registered_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    cancelled_at TIMESTAMPTZ,
    UNIQUE(event_id, member_profile_id)
);

CREATE TABLE event_checkin (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    registration_id UUID UNIQUE REFERENCES event_registration(id) ON DELETE CASCADE,
    checked_in_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    checked_in_by UUID REFERENCES account(id),
    is_valid BOOLEAN NOT NULL DEFAULT TRUE
);

-- ====================== Certificate & Honor ======================
CREATE TABLE certificate_type (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE certificate (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    member_profile_id UUID REFERENCES member_profile(id) ON DELETE CASCADE,
    certificate_type_id UUID REFERENCES certificate_type(id),
    certificate_no VARCHAR(100) UNIQUE NOT NULL,
    verification_code VARCHAR(100) UNIQUE NOT NULL,
    issue_date DATE NOT NULL,
    status certificate_status NOT NULL DEFAULT 'ACTIVE',
    issued_by UUID REFERENCES account(id)
);

CREATE TABLE honor_badge (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_public BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE member_honor (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    member_profile_id UUID REFERENCES member_profile(id) ON DELETE CASCADE,
    honor_badge_id UUID REFERENCES honor_badge(id),
    granted_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    granted_by UUID REFERENCES account(id)
);

-- ====================== Notification & Audit ======================
CREATE TABLE notification (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    channel notification_channel NOT NULL,
    created_by UUID REFERENCES account(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE notification_recipient (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    notification_id UUID REFERENCES notification(id) ON DELETE CASCADE,
    account_id UUID REFERENCES account(id) ON DELETE CASCADE,
    delivery_status delivery_status NOT NULL DEFAULT 'PENDING',
    read_at TIMESTAMPTZ
);

CREATE TABLE audit_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    actor_account_id UUID REFERENCES account(id),
    action VARCHAR(100) NOT NULL,
    target_type VARCHAR(100) NOT NULL,
    target_id UUID,
    logged_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    metadata_json JSONB
);

-- Indexes khuyến nghị (performance)
CREATE INDEX idx_account_email ON account(email);
CREATE INDEX idx_member_code ON member_profile(member_code);
CREATE INDEX idx_point_wallet_member ON point_wallet(member_profile_id);
CREATE INDEX idx_event_start ON event(start_at);
CREATE INDEX idx_audit_actor ON audit_log(actor_account_id);